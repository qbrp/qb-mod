package org.qbrp.main.engine.chat.core.system

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.util.networking.messages.Message
import org.qbrp.main.core.util.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.main.core.util.networking.messages.components.Cluster
import org.qbrp.main.core.util.networking.messaging.NetworkUtil
import org.qbrp.main.core.util.networking.messaging.ServerReceiver
import org.qbrp.main.core.util.networking.messaging.ServerReceiverContext
import org.qbrp.main.engine.players.model.ServerPlayerObject

class ServerChatNetworking(private val handler: MessageHandler, val server: MinecraftServer): ChatNetworking() {
    init {
        ServerReceiver<ServerReceiverContext>(SEND_MESSAGE, Cluster::class, { message, context, receiver ->
            handleMessagePacket(message)
            true
        }).register()
    }

    fun handleMessagePacket(message: Message) {
        handler.handleReceivedMessage(getChatMessage(message), this)
    }

    fun sendMessagePacket(player: ServerPlayerObject, message: ChatMessage) {
        server.playerManager.getPlayer(player.uuid.value)?.let { sendMessagePacket(it, message) }
    }

    fun sendMessagePacket(player: ServerPlayerEntity, message: ChatMessage) {
        NetworkUtil.sendMessage(
            Message(
                identifier = SEND_MESSAGE,
                content = message.toCluster()
            ),
            player
        )
    }
}