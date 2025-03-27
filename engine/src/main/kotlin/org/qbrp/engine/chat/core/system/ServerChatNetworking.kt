package org.qbrp.engine.chat.core.system

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.Messages.END_TYPING
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messages.Messages.START_TYPING
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.BooleanContent
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messaging.NetworkManager
import org.qbrp.system.networking.messaging.ServerReceiver
import org.qbrp.system.networking.messaging.ServerReceiverContext

class ServerChatNetworking(val handler: MessageHandler): ChatNetworking() {
    init {
        ServerReceiver<ServerReceiverContext>(SEND_MESSAGE, Cluster::class, { message, context, receiver ->
            handleMessagePacket(message)
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(START_TYPING, Signal::class, { message, context, receiver ->
            VisualDataStorage.getPlayer(context.player.name.string)?.apply {
                isWriting = true
                broadcastHardUpdate()
            }
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(END_TYPING, Signal::class, { message, context, receiver ->
            VisualDataStorage.getPlayer(context.player.name.string)?.apply {
                isWriting = false
                broadcastHardUpdate()
            }
            true
        }).register()
    }

    fun handleMessagePacket(message: Message) {
        handler.handleReceivedMessage(getChatMessage(message), this)
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