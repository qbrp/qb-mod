package org.qbrp.engine.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.engine.chat.system.MessageStorage

fun interface MessageEditedEvent {
    fun invokeEvent(message: ChatMessage, storage: MessageStorage): ActionResult

    companion object {
        val EVENT: Event<MessageEditedEvent> = EventFactory.createArrayBacked(
            MessageEditedEvent::class.java,
            { listeners: Array<out MessageEditedEvent> ->
                MessageEditedEvent { message, storage ->
                    for (listener in listeners) {
                        val result = listener.invokeEvent(message, storage)
                        if (result != ActionResult.PASS) {
                            return@MessageEditedEvent result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}