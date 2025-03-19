package org.qbrp.engine.chat.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.chat.core.messages.Sender

fun interface MessageUpdateEvent {
    fun onMessageUpdate(message: ChatMessage): ActionResult

    companion object {
        val EVENT: Event<MessageUpdateEvent> = EventFactory.createArrayBacked(
            MessageUpdateEvent::class.java,
            { listeners: Array<out MessageUpdateEvent> ->
                // Создаем функцию, которая будет вызывать всех слушателей
                MessageUpdateEvent { message ->
                    for (listener in listeners) {
                        val result = listener.onMessageUpdate(message)
                        if (result != ActionResult.PASS) {
                            return@MessageUpdateEvent result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}