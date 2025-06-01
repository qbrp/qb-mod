package org.qbrp.client.engine.chat.system

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.system.ChatNetworking
import org.qbrp.client.ClientCore
import org.qbrp.client.engine.chat.ClientChatAPI
import org.qbrp.client.engine.chat.system.events.MessageSendEvent
import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages.END_TYPING
import org.qbrp.main.core.utils.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.main.core.utils.networking.messages.Messages.START_TYPING
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.types.Signal

class ClientChatNetworking(val storage: MessageStorage): ChatNetworking() {

    fun registerReceivers() {
        ClientReceiver<ClientReceiverContext>(SEND_MESSAGE, Cluster::class) { message, context, receiver ->
            ClientCore.getAPI<ClientChatAPI>()?.handleMessageFromServer(message)
            true
        }.register()
    }

    fun sendStartTypingStatus() {
        ClientNetworkUtil.sendMessage(Message(
            identifier = START_TYPING,
            content = Signal()
        ))
    }

    fun sendEndTypingStatus() {
        ClientNetworkUtil.sendMessage(Message(
            identifier = END_TYPING,
            content = Signal()
        ))
    }

    fun handleMessagePacket(message: Message) {
        val chatMessage = getChatMessage(message)
        storage.addMessage(chatMessage)
    }

    fun sendMessagePacket(message: ChatMessage) {
        MessageSendEvent.EVENT.invoker().invokeEvent(message)
    }

}