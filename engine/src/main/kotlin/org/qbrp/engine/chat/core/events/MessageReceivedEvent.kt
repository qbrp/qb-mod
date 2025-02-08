package org.qbrp.engine.chat.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.messages.ChatMessage

fun interface MessageReceivedEvent {
    fun onMessageReceived(author: ServerPlayerEntity, message: ChatMessage): ActionResult

    companion object {
        val EVENT: Event<MessageReceivedEvent> = EventFactory.createArrayBacked(
            MessageReceivedEvent::class.java,
            { listeners: Array<out MessageReceivedEvent> ->
                // Создаем функцию, которая будет вызывать всех слушателей
                MessageReceivedEvent { author, message ->
                    for (listener in listeners) {
                        val result = listener.onMessageReceived(author, message)
                        if (result != ActionResult.PASS) {
                            return@MessageReceivedEvent result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}