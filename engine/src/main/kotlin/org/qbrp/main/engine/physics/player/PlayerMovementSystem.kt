package org.qbrp.main.engine.physics.player

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.fabricmc.yaw
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import org.qbrp.main.engine.world.location.asWorldVec3

class PlayerMovementSystem {
    fun update(playerList: PlayerList) {
        for (player in playerList) {
            val yaw = player.yaw
            val emulation = player.get<MovementEmulation>() ?: continue
            val input = player.get<MovementInput>() ?: continue
            val movement = player.get<PlayerMovementAbilities>() ?: continue
            val isSupported = player.isSupported

            emulation.tickInput(input, isSupported, movement.fly, 3.5)
            player.tickMovement(yaw, emulation, movement.fly)
        }
    }
}

class PlayerTeleportationSystem {
    fun update(playerList: ServerPlayerList) {
        for (player in playerList) {
            player.get<TeleportationComponent>()?.also { tp ->
                val x = tp.x
                val y = tp.y
                val z = tp.z
                player.setPosition(Vec3(x, y, z).asWorldVec3())
                if (tp.notify) {
                    player.sendChatMessage("Игрок перемещен на координаты $x, $y, $z")
                }
                player.removeComponent(tp)
            }
        }
    }
}