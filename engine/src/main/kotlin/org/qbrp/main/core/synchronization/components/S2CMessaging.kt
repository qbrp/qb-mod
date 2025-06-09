package org.qbrp.main.core.synchronization.components

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface S2CMessaging {
    val messageSender: MessagingChannelSender
    fun sendMessage(content: Cluster, player: ServerPlayerObject, id: String, messageSender: MessagingChannelSender) {
        messageSender.sendMessage(content, id, this as BaseObject, player)
    }
    fun sendMessage(content: Cluster, player: ServerPlayerObject, id: String) {
        sendMessage(content, player, id, messageSender)
    }
}