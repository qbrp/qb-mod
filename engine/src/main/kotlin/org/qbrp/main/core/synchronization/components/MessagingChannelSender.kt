package org.qbrp.main.core.synchronization.components

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface MessagingChannelSender {
    fun sendMessage(content: Cluster, id: String, obj: BaseObject, player: ServerPlayerObject?)
}