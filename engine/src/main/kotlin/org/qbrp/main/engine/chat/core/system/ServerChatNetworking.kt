package org.qbrp.main.engine.chat.core.system

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages.END_TYPING
import org.qbrp.main.core.utils.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.main.core.utils.networking.messages.Messages.START_TYPING
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.types.Signal
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext

class ServerChatNetworking(val handler: MessageHandler): ChatNetworking() {
    init {
        ServerReceiver<ServerReceiverContext>(SEND_MESSAGE, Cluster::class, { message, context, receiver ->
            handleMessagePacket(message)
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(START_TYPING, Signal::class, { message, context, receiver ->
            // TODO
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(END_TYPING, Signal::class, { message, context, receiver ->
            // TODO
            true
        }).register()
    }

    fun handleMessagePacket(message: Message) {
        handler.handleReceivedMessage(getChatMessage(message), this)
    }

    fun sendMessagePacket(player: ServerPlayerEntity, message: ChatMessage) {
        NetworkUtil.sendMessage(player,
            Message(
                identifier = SEND_MESSAGE,
                content = message.toCluster()
            )
        )
    }
}