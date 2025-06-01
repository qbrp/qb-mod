package org.qbrp.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.client.engine.chat.system.MessageStorage

fun interface MessageDeletedEvent {
    fun invokeEvent(message: ChatMessage, storage: MessageStorage): ActionResult

    companion object {
        val EVENT: Event<MessageDeletedEvent> = EventFactory.createArrayBacked(
            MessageDeletedEvent::class.java,
            { listeners: Array<out MessageDeletedEvent> ->
                MessageDeletedEvent { message, storage ->
                    for (listener in listeners) {
                        val result = listener.invokeEvent(message, storage)
                        if (result != ActionResult.PASS) {
                            return@MessageDeletedEvent result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}