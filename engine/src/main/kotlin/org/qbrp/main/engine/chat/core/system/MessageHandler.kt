package org.qbrp.main.engine.chat.core.system

import net.minecraft.server.MinecraftServer
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.main.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.chat.core.messages.Sender

class MessageHandler(val server: MinecraftServer) {

    fun handleReceivedMessage(message: ChatMessage, responseNetworking: ServerChatNetworking) {
        val time = System.nanoTime()
        if (MessageReceivedEvent.Companion.EVENT.invoker().onMessageReceived(message) != ActionResult.FAIL) {
            getSender(message, responseNetworking).send(message)
        }
        val timeTotal = System.nanoTime() - time
        println("Время, затраченное на обработку: ${timeTotal / 1000000} ms (${timeTotal})")
    }

    fun getSender(message: ChatMessage, responseNetworking: ServerChatNetworking): Sender {
        val sender = MessageSender(responseNetworking, mutableListOf())
        MessageSenderPipeline.Companion.EVENT.invoker().onMessageSenderInitialization(message, sender)
        return sender
    }

}