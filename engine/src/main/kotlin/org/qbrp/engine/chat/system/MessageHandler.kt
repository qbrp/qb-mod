package org.qbrp.engine.chat.system

import net.minecraft.server.MinecraftServer
import org.qbrp.engine.chat.events.MessageReceivedEvent
import org.qbrp.engine.chat.events.MessageSendEvent
import org.qbrp.engine.chat.events.MessageSenderPipeline
import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.chat.messages.MessageSender
import org.qbrp.engine.chat.messages.Sender

class MessageHandler(val server: MinecraftServer) {

    fun handleReceivedMessage(message: ChatMessage, responseNetworking: ServerChatNetworking) {
        val author = server.playerManager.getPlayer(message.authorName)!!
        MessageReceivedEvent.EVENT.invoker().onMessageReceived(author, message)
        getSender(message, responseNetworking).send(message)
    }

    fun getSender(message: ChatMessage, responseNetworking: ServerChatNetworking): Sender {
        val sender = MessageSender(responseNetworking, mutableListOf())
        MessageSenderPipeline.EVENT.invoker().onMessageSenderInitialization(message, sender)
        return sender
    }

}