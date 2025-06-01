package org.qbrp.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.client.engine.chat.system.Typer

fun interface ChatInputParseEvent {
    fun handleMessage(message: String, context: Typer.TypingMessageContext): ActionResult

    companion object {
        val EVENT: Event<ChatInputParseEvent> = EventFactory.createArrayBacked(
            ChatInputParseEvent::class.java,
            { listeners: Array<out ChatInputParseEvent> ->
                ChatInputParseEvent { message, context ->
                    for (listener in listeners) {
                        val result = listener.handleMessage(message, context)
                        if (result != ActionResult.PASS) {
                            return@ChatInputParseEvent result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}