package org.qbrp.engine.chat.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import org.qbrp.engine.chat.core.messages.ChatMessage

fun interface ChatFormatEvent {
    fun handleMessage(message: ChatMessage, text: String): String

    companion object {
        val EVENT: Event<ChatFormatEvent> = EventFactory.createArrayBacked(
            ChatFormatEvent::class.java
        ) { listeners: Array<out ChatFormatEvent> ->
            ChatFormatEvent { message, text ->
                var modifiedText = text
                for (listener in listeners) {
                    modifiedText = listener.handleMessage(message, modifiedText)
                }
                modifiedText
            }
        }
    }
}
