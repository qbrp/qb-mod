package org.qbrp.main.engine.players.model.service

import net.minecraft.entity.Entity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.utils.getPlayersInRadius
import org.qbrp.main.engine.players.model.ServerPlayerObject

object PlayersUtil: KoinComponent {
    private val serverPlayerStorage by inject<ServerPlayerStorage>()
    private val server by inject<MinecraftServer>()

    fun getPlayer(name: String) = server.playerManager.getPlayer(name)
    fun getPlayerSession(name: String): ServerPlayerObject? = serverPlayerStorage.getByPlayerName(name)
    fun getPlayerSession(player: ServerPlayerEntity): ServerPlayerObject = serverPlayerStorage.getByPlayer(player)

    context(ctx: MinecraftContext)
    fun getPlayerLookingAt(player: ServerPlayerObject) = getPlayerLookingAt(player.getEntity())

    fun getPlayersInRadius(entity: ServerPlayerEntity, gameMode: GameMode? = null): List<ServerPlayerEntity> {
        return entity.world.server!!.playerManager.playerList.getPlayersInRadius(entity, 5.0)
            .filter { it.interactionManager?.gameMode != GameMode.SPECTATOR }
            .filter { if (gameMode != null) it.interactionManager?.gameMode == gameMode else true }
    }

    fun getPlayerLookingAt(player: ServerPlayerEntity,
                           gameMode: GameMode? = null,
                           entities: List<Entity> = getPlayersInRadius(player)): Entity? {
        val lookDirection = player.rotationVector
        val start = player.eyePos
        val end = start.add(lookDirection.multiply(5.0))

        player.raycast(0.0, 0f, false)

        player.boundingBox.raycast(start, end)?.let { hitResult ->
            val pos = hitResult.get()
            val block = player.serverWorld.getBlockState(
                BlockPos(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())
            )
        }

        var closestEntity: Entity? = null
        var closestDistance = Double.MAX_VALUE

        for (entity in entities) {
            val hitResult = entity.boundingBox.raycast(start, end)
            if (hitResult.isPresent) {
                val hitPos = hitResult.get()
                val distance = start.distanceTo(hitPos)
                if (distance < closestDistance) {
                    closestEntity = entity
                    closestDistance = distance
                }
            }
        }

        return closestEntity
    }
}