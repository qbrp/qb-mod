package org.qbrp.engine.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.client.engine.chat.system.Typer

fun interface ChatFormatEvent {
    fun handleMessage(message: ChatMessage): ActionResult

    companion object {
        val EVENT: Event<ChatFormatEvent> = EventFactory.createArrayBacked(
            ChatFormatEvent::class.java,
            { listeners: Array<out ChatFormatEvent> ->
                ChatFormatEvent { message ->
                    for (listener in listeners) {
                        val result = listener.handleMessage(message)
                        if (result != ActionResult.PASS) {
                            return@ChatFormatEvent result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}