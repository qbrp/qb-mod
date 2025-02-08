package org.qbrp.engine.chat.core.system

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.CHAT_GROUPS
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messages.types.ClusterListContent
import org.qbrp.system.networking.messaging.NetworkManager


class ServerChatNetworking(val handler: MessageHandler): ChatNetworking() {
    fun handleMessagePacket(message: Message) {
        handler.handleReceivedMessage(getChatMessage(message), this)
    }

    fun sendGroupsList(player: ServerPlayerEntity, groups: ChatGroups) {
        val clusterGroups = groups.getAllGroups().map { group -> group.toCluster() }
        NetworkManager.sendMessage(player,
            Message(
                CHAT_GROUPS,
                ClusterListContent().apply { list =  clusterGroups}
            )
        )
    }

    fun sendMessagePacket(player: ServerPlayerEntity, message: ChatMessage) {
        NetworkManager.sendMessage(player,
            Message(
                identifier = SEND_MESSAGE,
                content = message.toCluster()
            )
        )
    }

}