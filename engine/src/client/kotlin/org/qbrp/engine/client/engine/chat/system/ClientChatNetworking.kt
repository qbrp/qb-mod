package org.qbrp.engine.client.engine.chat.system

import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.chat.system.ChatNetworking
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.END_TYPING
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messages.Messages.START_TYPING
import org.qbrp.system.networking.messages.types.Signal

class ClientChatNetworking(val storage: MessageStorage): ChatNetworking() {

    fun sendStartTypingStatus() {
        ClientNetworkManager.sendMessage(Message(
            identifier = START_TYPING,
            content = Signal()
        ))
    }

    fun sendEndTypingStatus() {
        ClientNetworkManager.sendMessage(Message(
            identifier = END_TYPING,
            content = Signal()
        ))
    }

    fun handleMessagePacket(message: Message) {
        val chatMessage = getChatMessage(message)
        storage.addMessage(chatMessage)
    }

    fun sendMessagePacket(message: ChatMessage) {
        ClientNetworkManager.sendMessage(
            Message(
                identifier = SEND_MESSAGE,
                content = message.toCluster()
            )
        )
    }

}