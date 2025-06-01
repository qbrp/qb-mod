package org.qbrp.main.engine.chat.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender

fun interface MessageSenderPipeline {
    fun onMessageSenderInitialization(message: ChatMessage, messageSender: MessageSender): ActionResult

    companion object {
        val EVENT: Event<MessageSenderPipeline> = EventFactory.createArrayBacked(
            MessageSenderPipeline::class.java,
            { listeners: Array<out MessageSenderPipeline> ->
                // Создаем функцию, которая будет вызывать всех слушателей
                MessageSenderPipeline { message, sender ->
                    for (listener in listeners) {
                        val result = listener.onMessageSenderInitialization(message, sender)
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