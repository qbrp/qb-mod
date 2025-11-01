package org.qbrp.main.engine.world

import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.core.util.math.engine
import org.qbrp.main.core.util.math.toMinecraft
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.physics.player.MinecraftEntities
import org.qbrp.main.engine.physics.player.ServerMinecraftEntities
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.players.model.world
import org.qbrp.main.engine.props.WorldPropManager
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.model.MainWorld
import org.qbrp.main.engine.world.model.World
import kotlin.jvm.optionals.getOrNull

@Autoload(8)
class WorldModule : EngineModule("world"), WorldRaycastManager {
    private val mainWorld by inject<MainWorld>()
    private val bodyManager by inject<WorldBodyManager>()
    private val propsManager by inject<WorldPropManager>()
    private val playerStorage by inject<ServerPlayerStorage>()
    private val minecraftEntities by inject<ServerMinecraftEntities>()

    override fun getKoinModule() = module {
        single { MainWorld(get()) }
        single<World> { get<MainWorld>() }
        single<WorldRaycastManager> { this@WorldModule }
    }

    override fun raycast(
        start: WorldPos,
        end: WorldPos,
        filter: RaycastFilter
    ): RaycastResult {
        val targets = filter.targets
        val bodies = bodyManager.raycast(start, end)
        val props = if (targets.contains(RaycastTarget.PROP)) bodies.mapNotNull { propsManager.getProp(it) } else listOf()
        val start = start.asVec3()
        val end = end.asVec3()

        val players = playerStorage.filter { it.world == mainWorld }
        val hitPlayers = if (targets.contains(RaycastTarget.PLAYER))
            players.mapNotNull { player ->
                player.raycastBoundingBox(start.v, end.v)?.let { player to it }
            }
                .sortedBy { (_, hitPoint) -> hitPoint.squaredDistanceTo(start) }
                .map { (player, _) -> player }
        else listOf()

        return RaycastResult(props, hitPlayers)
    }

    fun PlayerReference.raycastBoundingBox(min: Vec3, max: Vec3): Vec3? {
        val entity = minecraftEntities.getEntity(uuid) ?: return null
        return entity.boundingBox.raycast(min.toMinecraft(), max.toMinecraft()).getOrNull()?.engine()
    }
}