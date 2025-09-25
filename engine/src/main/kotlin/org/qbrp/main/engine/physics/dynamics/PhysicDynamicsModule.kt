package org.qbrp.main.engine.physics.dynamics

import com.github.stephengold.joltjni.BroadPhaseLayerInterfaceTable
import com.github.stephengold.joltjni.JobSystem
import com.github.stephengold.joltjni.JobSystemThreadPool
import com.github.stephengold.joltjni.Jolt
import com.github.stephengold.joltjni.JoltPhysicsObject
import com.github.stephengold.joltjni.ObjectLayerPairFilterTable
import com.github.stephengold.joltjni.ObjectVsBroadPhaseLayerFilterTable
import com.github.stephengold.joltjni.PhysicsSystem
import com.github.stephengold.joltjni.TempAllocator
import com.github.stephengold.joltjni.TempAllocatorMalloc
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.koin.core.component.get
import org.koin.core.module.Module
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.mc.chunks.MainWorldBlockEvents
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.physics.dynamics.CollisionLayers.PLAYER
import org.qbrp.main.engine.physics.dynamics.CollisionLayers.PROP
import org.qbrp.main.engine.physics.dynamics.CollisionLayers.WORLD
import org.qbrp.main.engine.physics.dynamics.chunk.factory.ChunkColliderFactory
import org.qbrp.main.engine.physics.dynamics.chunk.factory.ChunkColliderFactoryImpl
import org.qbrp.main.engine.physics.dynamics.chunk.loader.ChunkAllocator
import org.qbrp.main.engine.physics.dynamics.chunk.loader.ChunkLoadDebugger
import org.qbrp.main.engine.physics.dynamics.chunk.loader.PhysicsChunkManager
import org.qbrp.main.engine.physics.dynamics.debug.PhysicsCommand
import org.qbrp.main.engine.physics.dynamics.events.BodyEvents
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.physics.dynamics.manipulate.ManipulatorImpl
import org.qbrp.main.engine.physics.dynamics.simulation.BodyFactory
import org.qbrp.main.engine.physics.dynamics.simulation.JoltBodyFactory
import org.qbrp.main.engine.physics.dynamics.simulation.JoltShapeFactory
import org.qbrp.main.engine.physics.dynamics.simulation.Physics
import org.qbrp.main.engine.physics.dynamics.simulation.PhysicsSimulation
import org.qbrp.main.engine.physics.dynamics.simulation.WorldBodyManager
import org.qbrp.main.engine.physics.dynamics.simulation.WorldGround
import org.qbrp.main.engine.physics.dynamics.tracking.PlaceBreakBlockPropActivator
import org.qbrp.main.engine.physics.dynamics.tracking.PlayerTrackingPositions
import org.qbrp.main.engine.physics.dynamics.tracking.PositionTracker

@Autoload
class PhysicDynamicsModule() : EngineModule("physics-dynamics"), PhysicSystemConfiguration {
    override fun getKoinModule(): Module = innerWithApi({
        single<PhysicSystemConfiguration> { this@PhysicDynamicsModule }
        single<BodyFactory> { JoltBodyFactory(getLocal(), getLocal()) }
        single { Physics(get(), getLocal(), getLocal(), getLocal(), get(), getLocal()) }
        single<WorldBodyManager>() { get<Physics>() }
        single<PhysicsSimulation>() { get<Physics>() }
        single<ChunkColliderFactory> { ChunkColliderFactoryImpl() }
        single { ChunkAllocator(get(), get(), get()) }
        single<PhysicsChunkManager> { get<ChunkAllocator>() }
        single { PlayerTrackingPositions(get()) }
        single { PositionTracker(get(), get(), mutableListOf(get<PlayerTrackingPositions>())) }
        single { PlaceBreakBlockPropActivator(get()) }
        single<ChunkLoadDebugger> { get<ChunkAllocator>() }
        single<Manipulator> { getLocal<ManipulatorImpl>() }
    }, {
        scoped { JoltShapeFactory() }
        scoped<JobSystem> { newJobSystem() }
        scoped<TempAllocator> { TempAllocatorMalloc() }
        scoped { JoltNativeLibraryLoader() }
        scoped<PhysicsSystem> { buildUpPhysicsSystem() }
        scoped { ManipulatorImpl(get()) }
    })

    override val maxBodies: Int = 5000
    override val numBodyMutexes: Int = 0
    override val maxBodyPairs: Int = 65_536
    override val maxContacts: Int = 20_480

    override fun onEnable() {
        getLocal<JoltNativeLibraryLoader>().load()
        val physics = get<Physics>()
        val tracker = get<PositionTracker>()
        val activator = get<PlaceBreakBlockPropActivator>()
        physics.run()

        var counter = 0
        ServerTickEvents.END_WORLD_TICK.register {
            if (counter++ > 20) { tracker.trackAndUpsert(); counter = 0 }
            physics.step(0.05f)
        }
        MainWorldBlockEvents.ADDED.register { pos -> tracker.trackAndUpsert(); activator.onBlockUpdated(pos) }
        MainWorldBlockEvents.BREAK.register { pos -> tracker.trackAndUpsert(); activator.onBlockUpdated(pos) }
        BodyEvents.ADDED.register { body, manager ->
            tracker.handleAddedBody(body, manager)
        }

        WorldGround().create(get(), get())

        withEventContext {
            on<ModLoadedEvent> {
                get<CommandsAPI>().add(PhysicsCommand(get(), get(), get()))
            }
        }
    }

    private fun newJobSystem(): JobSystem {
        val numWorkerThreads = Runtime.getRuntime().availableProcessors();
        return JobSystemThreadPool(Jolt.cMaxPhysicsJobs, Jolt.cMaxPhysicsBarriers, numWorkerThreads);
    }

    private fun buildUpPhysicsSystem(): PhysicsSystem {
        JoltPhysicsObject.startCleaner()
        Jolt.registerDefaultAllocator()
        Jolt.installDefaultAssertCallback();
        Jolt.installDefaultTraceCallback();

        val success = Jolt.newFactory()
        require(success)
        Jolt.registerTypes()

        //Слои: Обрабатываются только столкновения игроков с пропами. Игроки не могут толкать друг друга
        val numObjLayers = 3;
        val numBpLayers = 3;
        val BP_DYNAMIC = 0
        val BP_STATIC  = 1

        val ovoFilter = ObjectLayerPairFilterTable(numObjLayers).apply {
            enableCollision(PLAYER, PROP)
            enableCollision(PROP, PLAYER)
            enableCollision(PROP, PROP)
            enableCollision(PROP, WORLD)
            enableCollision(WORLD, PROP)
            disableCollision(PLAYER, PLAYER)
        }

        val layerMap = BroadPhaseLayerInterfaceTable(numObjLayers, numBpLayers).apply {
            mapObjectToBroadPhaseLayer(PLAYER, BP_DYNAMIC)
            mapObjectToBroadPhaseLayer(PROP, BP_DYNAMIC)
            mapObjectToBroadPhaseLayer(WORLD, BP_STATIC)
        }

        val ovbFilter = ObjectVsBroadPhaseLayerFilterTable(layerMap, numBpLayers, ovoFilter, numObjLayers);
        val config = get<PhysicSystemConfiguration>()
        return PhysicsSystem().apply {
            init(config.maxBodies,
                config.numBodyMutexes,
                config.maxBodyPairs,
                config.maxContacts,
                layerMap,
                ovbFilter,
                ovoFilter)
            setGravity(0f, 0.1f, 0f)
        }
    }
}