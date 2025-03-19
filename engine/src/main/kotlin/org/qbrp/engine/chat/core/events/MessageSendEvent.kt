package org.qbrp.engine.chat.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.addons.tools.MessageTextEvents
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.chat.core.messages.Sender
import org.qbrp.engine.chat.core.system.ChatNetworking
import org.qbrp.engine.chat.core.system.ServerChatNetworking

fun interface MessageSendEvent {
    fun onMessageSend(sender: MessageSender, message: ChatMessage, receiver: ServerPlayerEntity, networking: ServerChatNetworking): ActionResult

    companion object {
        val EVENT: Event<MessageSendEvent> = EventFactory.createArrayBacked(
            MessageSendEvent::class.java,
            { listeners: Array<out MessageSendEvent> ->
                // Создаем функцию, которая будет вызывать всех слушателей
                MessageSendEvent { sender, message, receiver, networking ->
                    for (listener in listeners) {
                        val result = listener.onMessageSend(sender, message, receiver, networking)
                        //println("Обработка отправки для ${receiver.name.string}: $message")
                        println("   Обработка (${receiver.name.string} <- ${message.getText()})")
                        if (result == ActionResult.FAIL) {
                            return@MessageSendEvent ActionResult.FAIL
                        }
                        if (result != ActionResult.PASS) {
                            return@MessageSendEvent result
                        }
                    }
                    // Если все слушатели вернули PASS, отправляем сообщение
                    MessageTextEvents.pasteText(message)
                    networking.sendMessagePacket(receiver, message)
                    println("${receiver.name.string} <- ${message.getText()}")
                    ActionResult.SUCCESS
                }
            }
        )
    }
}