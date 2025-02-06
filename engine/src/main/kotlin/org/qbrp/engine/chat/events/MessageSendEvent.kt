package org.qbrp.engine.chat.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.chat.messages.MessageSender
import org.qbrp.engine.chat.messages.Sender

fun interface MessageSendEvent {
    fun onMessageSend(sender: Sender, message: ChatMessage): ActionResult

    companion object {
        val EVENT: Event<MessageSendEvent> = EventFactory.createArrayBacked(
            MessageSendEvent::class.java,
            { listeners: Array<out MessageSendEvent> ->
                // Создаем функцию, которая будет вызывать всех слушателей
                MessageSendEvent { targets, message ->
                    for (listener in listeners) {
                        val result = listener.onMessageSend(targets, message)
                        if (result != ActionResult.PASS) {
                            return@MessageSendEvent result
                        }
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}