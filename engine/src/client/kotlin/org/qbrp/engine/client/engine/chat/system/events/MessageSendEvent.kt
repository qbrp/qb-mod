package org.qbrp.engine.client.engine.chat.system.events

import klite.info
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.utils.log.Loggers

fun interface MessageSendEvent {
    fun invokeEvent(message: ChatMessage): ActionResult

    companion object {
        val logger = Loggers.get("chat", "sending")
        val EVENT: Event<MessageSendEvent> = EventFactory.createArrayBacked(
            MessageSendEvent::class.java,
            { listeners: Array<out MessageSendEvent> ->
                MessageSendEvent { message ->
                    for (listener in listeners) {
                        val result = listener.invokeEvent(message)
                        if (result != ActionResult.PASS) {
                            return@MessageSendEvent result
                        }
                    }
                    ClientNetworkManager.sendMessage(
                        Message(
                            identifier = SEND_MESSAGE,
                            content = message.toCluster()
                        ).also {
                            logger.log("Отправлено сообщение:\n" +
                                    "ID: ${message.uuid}\n" +
                                    "Теги: ${message.getTags().toList()}\n" +
                                    "Текст: ${message.getText()}")
                        }
                    )
                    ActionResult.SUCCESS
                }
            }
        )
    }
}