package org.qbrp.main.core.mc.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.Core
import org.qbrp.main.core.utils.world.getPlayersInRadius

object PlayersUtil: KoinComponent {
    fun getPlayer(name: String) = Core.server.playerManager.getPlayer(name)
    fun getPlayerSession(name: String): PlayerObject? = get<PlayersAPI>().getPlayerSession(name)
    fun getPlayerSession(player: ServerPlayerEntity): PlayerObject = get<PlayersAPI>().getPlayerSession(player)
    fun getPlayerSessionOrNull(player: ServerPlayerEntity): PlayerObject? = get<PlayersAPI>().getPlayerSessionOrNull(player)

    fun getLookDirection(player: PlayerEntity): Vec3d {
        val rotation = player.rotationVector
        return Vec3d(rotation.x, rotation.y, rotation.z)
    }

    fun getPlayerLookingAt(player: PlayerObject) = getPlayerLookingAt(player.entity)

    fun getPlayerLookingAt(player: ServerPlayerEntity, gameMode: GameMode? = null): ServerPlayerEntity? {
        val world = player.world
        val lookDirection = player.rotationVector // Предполагается, что это направление взгляда
        val start = player.eyePos // Начальная точка луча (глаза игрока)
        val end = start.add(lookDirection.multiply(5.0)) // Конечная точка луча (5 блоков)

        val entities = world.server!!.playerManager.playerList.getPlayersInRadius(player, 5.0)
            .filter { it.interactionManager?.gameMode != GameMode.SPECTATOR }
            .filter { if (gameMode != null) it.interactionManager?.gameMode == gameMode else true }

        var closestEntity: ServerPlayerEntity? = null
        var closestDistance = Double.MAX_VALUE

        // Проверяем каждую сущность
        for (entity in entities) {
            // Проверяем пересечение луча с ограничивающей рамкой сущности
            val hitResult = entity.boundingBox.raycast(start, end)
            if (hitResult.isPresent) {
                val hitPos = hitResult.get() // Точка пересечения
                val distance = start.distanceTo(hitPos) // Расстояние до точки пересечения
                if (distance < closestDistance) {
                    closestEntity = entity as ServerPlayerEntity
                    closestDistance = distance
                }
            }
        }

        return closestEntity
    }

    val playersList
        get() = get<PlayersAPI>().getPlayers()

}