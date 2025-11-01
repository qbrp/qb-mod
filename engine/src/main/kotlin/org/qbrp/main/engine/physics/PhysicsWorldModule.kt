package org.qbrp.main.engine.physics

import net.minecraft.util.math.BlockPos
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.main.core.events.on
import org.qbrp.main.core.fabricmc.chunks.MainWorldBlockEvents
import org.qbrp.main.core.fabricmc.commands.registerCommand
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.util.math.engine
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.physics.chunk.ChunkBank
import org.qbrp.main.engine.physics.chunk.PhysicsChunkManager
import org.qbrp.main.engine.physics.player.JoltPlayerBodyManager
import org.qbrp.main.engine.physics.player.PlayerBodyManager
import org.qbrp.main.engine.physics.player.PlayerMovementSystem
import org.qbrp.main.engine.physics.system.PhysicsCommand
import org.qbrp.main.engine.physics.system.PhysicsSystemEvents
import org.qbrp.main.engine.physics.system.service.PhysicsSimulation
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.physics.system.service.createWorldGround
import org.qbrp.main.engine.physics.tracking.PlayerTrackingPositions
import org.qbrp.main.engine.physics.tracking.activateNearestBodies
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

@Autoload
class PhysicsWorldModule() : EngineModule("physics-world") {
    override fun getKoinModule(): Module = innerWithApi({
        single<PhysicsChunkManager> { getLocal<ChunkBank>() }
        single { PlayerTrackingPositions(get()) }
        single {
            ChunkLoadSchedulerSystem(
                getLocal(),
                get(),
                get(),
                mutableListOf(get<PlayerTrackingPositions>())
            )
        }
        single<PlayerBodyManager> { JoltPlayerBodyManager(get(), get(), get<ServerPlayerStorage>()) }
    }, {
        scoped { ChunkBank(get()) }
    })

    private val physics by inject<PhysicsSimulation>()
    private val tracker by inject<ChunkLoadSchedulerSystem>()
    private val worldBodyManager by inject<WorldBodyManager>()

    override fun onEnable() {
        tracker.startTrackThread()

        MainWorldBlockEvents.ADDED.register(::updateBlocksAt)
        MainWorldBlockEvents.BREAK.register(::updateBlocksAt)

        on<PhysicsSystemEvents.BodyAdd> {
            tracker.handleAddedBody(body)
        }

        worldBodyManager.createWorldGround(get())

        on<ModLoadedEvent>() {
            registerCommand(PhysicsCommand(get(), get(), get()))
        }
    }

    private fun updateBlocksAt(pos: BlockPos) {
        tracker.markDirty(pos.toCenterPos().engine())
        tracker.trackAndUpsert()
        worldBodyManager.activateNearestBodies(pos, ACTIVATION_DISTANCE)
    }

    companion object {
        const val ACTIVATION_DISTANCE = 12.0
    }
}