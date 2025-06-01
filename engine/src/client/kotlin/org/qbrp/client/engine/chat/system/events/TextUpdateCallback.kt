package org.qbrp.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.hud.ChatHudLine
import org.qbrp.client.engine.chat.system.HandledMessage

fun interface TextUpdateCallback {
    fun modifyText(original: List<ChatHudLine.Visible>, message: HandledMessage): List<ChatHudLine.Visible>?

    companion object {
        val EVENT: Event<TextUpdateCallback> = EventFactory.createArrayBacked(
            TextUpdateCallback::class.java
        ) { listeners ->
            TextUpdateCallback { text, message ->
                listeners.fold(text) { current, listener ->
                    // Вызываем modifyText и проверяем результат
                    listener.modifyText(current, message) ?: current
                }
            }
        }
    }
}