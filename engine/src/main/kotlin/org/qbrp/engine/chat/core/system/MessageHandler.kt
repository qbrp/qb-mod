package org.qbrp.engine.chat.core.system

import net.minecraft.server.MinecraftServer
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.chat.core.messages.Sender

class MessageHandler(val server: MinecraftServer) {

    fun handleReceivedMessage(message: ChatMessage, responseNetworking: ServerChatNetworking) {
        val author = server.playerManager.getPlayer(message.authorName)!!
        MessageReceivedEvent.Companion.EVENT.invoker().onMessageReceived(author, message)
        getSender(message, responseNetworking).send(message)
    }

    fun getSender(message: ChatMessage, responseNetworking: ServerChatNetworking): Sender {
        val sender = MessageSender(responseNetworking, mutableListOf())
        MessageSenderPipeline.Companion.EVENT.invoker().onMessageSenderInitialization(message, sender)
        return sender
    }

}