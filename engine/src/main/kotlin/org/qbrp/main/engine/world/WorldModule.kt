package org.qbrp.main.engine.world

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.main.core.mc.context.withMinecraftContext
import org.qbrp.main.core.mc.player.raycastBoundingBox
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.simulation.WorldBodyManager
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.props.world.WorldPropManager
import org.qbrp.main.engine.world.model.MainWorld
import org.qbrp.main.engine.world.model.World

@Autoload(8)
class WorldModule : EngineModule("world"), WorldRaycastManager {
    private val mcWorld: ServerWorld by lazy { get<MinecraftServer>().overworld }
    private val bodyManager by inject<WorldBodyManager>()
    private val propsManager by inject<WorldPropManager>()
    private val playerStorage by inject<ServerPlayerStorage>()

    override fun getKoinModule() = module {
        single { MainWorld(get(), get()) }
        single<World> { get<MainWorld>() }
        single<WorldRaycastManager> { this@WorldModule }
    }

    override fun onEnable() {
        val mainWorld: MainWorld by inject()
        ServerLifecycleEvents.SERVER_STARTED.register {
            val world = get<MinecraftServer>().overworld
            mainWorld.mcWorld = world
        }
    }

    override fun raycast(
        start: Position,
        end: Position,
        filter: RaycastFilter
    ): RaycastResult = withMinecraftContext {
        val targets = filter.targets
        val bodies = bodyManager.raycast(start, end)
        val props = if (targets.contains(RaycastTarget.PROP)) bodies.mapNotNull { propsManager.getProp(it) } else listOf()

        val players = playerStorage.filter { it.context.world == mcWorld }
        val hitPlayers = if (targets.contains(RaycastTarget.PLAYER))
            players.mapNotNull { player ->
                player.raycastBoundingBox(start, end)?.let { player to it }
            }
                .sortedBy { (_, hitPoint) -> hitPoint.distanceTo(start) }
                .map { (player, _) -> player }
        else listOf()

        RaycastResult(props, hitPlayers)
    }
}