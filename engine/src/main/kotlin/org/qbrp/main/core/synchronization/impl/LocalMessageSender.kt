package org.qbrp.main.core.synchronization.impl

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messaging.NetworkMessageSender
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.core.synchronization.components.MessagingChannelSender

open class LocalMessageSender(val name: String, val networking: NetworkMessageSender = NetworkUtil): MessagingChannelSender {
    override fun sendMessage(content: Cluster, id: String, obj: BaseObject, player: ServerPlayerObject?) {
        val content = ClusterBuilder()
            .component("id", obj.id)
            .component("messaging.id", id)
            .component("messaging.content", content)
            .build()
        val msg = Message(Messages.syncChannel(name), content)
        sendMessage(msg, player)
    }

    private fun sendMessage(message: Message, playerObject: ServerPlayerObject?) {
        networking.sendMessage(message, playerObject?.entity)
    }
}