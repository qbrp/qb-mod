package org.qbrp.main.engine.chat.core.system

import net.minecraft.server.MinecraftServer
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.engine.players.model.ServerPlayerObject

class ServerChatNetworking(private val handler: MessageHandler, private val server: MinecraftServer): ChatNetworking() {
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
        NetworkUtil.sendMessage(
            Message(
                identifier = SEND_MESSAGE,
                content = message.toCluster()
            ),
            server.playerManager.getPlayer(player.uuid.value)!!
        )
    }
}