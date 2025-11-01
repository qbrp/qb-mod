package org.qbrp.client.core.mc

import org.litote.kmongo.or
import org.qbrp.client.engine.players.ClientMinecraftEntities
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.fabricmc.CameraOrientationComponent
import org.qbrp.main.engine.players.model.service.PlayerList

class CameraOrientationSystem {
    fun update(playerList: PlayerList, entities: ClientMinecraftEntities) {
        for (player in playerList) {
            val orientation = player.get<CameraOrientationComponent>() ?: return
            val entity = entities.getEntity(player.playerId) ?: return
            orientation.apply {
                yaw = entity.yaw.toDouble()
                pitch = entity.pitch.toDouble()
            }
        }
    }
}