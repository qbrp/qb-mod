package org.qbrp.main.engine.synchronization.components

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface LocalPlayerMessageSender {
    val messageSender: ObjectMessageSender
    fun sendMessage(content: Cluster, player: PlayerObject, id: String) {
        messageSender.sendMessage(content, id, this as BaseObject, player)
    }
}