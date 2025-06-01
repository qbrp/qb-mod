package org.qbrp.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

fun interface ChatInputEditEvent {
    fun transform(text: String): String

    companion object {
        val EVENT: Event<ChatInputEditEvent> = EventFactory.createArrayBacked(
            ChatInputEditEvent::class.java
        ) { listeners: Array<out ChatInputEditEvent> ->
            ChatInputEditEvent { text ->
                var transformedText = text
                for (listener in listeners) {
                    transformedText = listener.transform(transformedText)
                }
                transformedText
            }
        }
    }
}
