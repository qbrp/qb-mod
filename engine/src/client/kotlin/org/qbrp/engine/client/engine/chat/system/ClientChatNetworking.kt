package org.qbrp.engine.client.engine.chat.system

import org.qbrp.engine.Engine
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.ChatNetworking
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.chat.ClientChatAPI
import org.qbrp.engine.client.engine.chat.system.events.MessageSendEvent
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.END_TYPING
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messages.Messages.START_TYPING
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.Signal

class ClientChatNetworking(val storage: MessageStorage): ChatNetworking() {

    fun registerReceivers() {
        ClientReceiver<ClientReceiverContext>(SEND_MESSAGE, Cluster::class) { message, context, receiver ->
            Engine.getAPI<ClientChatAPI>()?.handleMessageFromServer(message)
            true
        }.register()
    }

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
        MessageSendEvent.EVENT.invoker().invokeEvent(message)
    }

}