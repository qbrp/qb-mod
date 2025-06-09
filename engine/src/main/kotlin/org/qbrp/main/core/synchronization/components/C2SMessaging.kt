package org.qbrp.main.core.synchronization.components

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface C2SMessaging {
    val messageSender: MessagingChannelSender
    fun sendMessage(content: Cluster, id: String, messageSender: MessagingChannelSender) {
        messageSender.sendMessage(content, id, this as BaseObject, null)
    }
    fun sendMessage(content: Cluster, id: String) {
        sendMessage(content, id, messageSender)
    }
}