package org.qbrp.engine.chat.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.chat.messages.MessageSender

fun interface MessageSenderPipeline {
    fun onMessageSenderInitialization(message: ChatMessage, messageSender: MessageSender): ActionResult

    companion object {
        val EVENT: Event<MessageSenderPipeline> = EventFactory.createArrayBacked(
            MessageSenderPipeline::class.java,
            { listeners: Array<out MessageSenderPipeline> ->
                // Создаем функцию, которая будет вызывать всех слушателей
                MessageSenderPipeline { author, sender ->
                    for (listener in listeners) {
                        val result = listener.onMessageSenderInitialization(author, sender)
                        if (result != ActionResult.PASS) {
                            return@MessageSenderPipeline result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}