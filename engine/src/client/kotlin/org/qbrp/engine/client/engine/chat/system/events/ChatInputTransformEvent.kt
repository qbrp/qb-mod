package org.qbrp.engine.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.engine.client.engine.chat.system.Typer

fun interface ChatInputTransformEvent {
    fun transform(text: String): String

    companion object {
        val EVENT: Event<ChatInputTransformEvent> = EventFactory.createArrayBacked(
            ChatInputTransformEvent::class.java
        ) { listeners: Array<out ChatInputTransformEvent> ->
            ChatInputTransformEvent { text ->
                var transformedText = text
                for (listener in listeners) {
                    transformedText = listener.transform(transformedText)
                }
                transformedText
            }
        }
    }
}
