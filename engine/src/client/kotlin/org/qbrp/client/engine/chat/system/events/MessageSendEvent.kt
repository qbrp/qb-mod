package org.qbrp.client.engine.chat.system.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.main.core.utils.log.LoggerUtil

fun interface MessageSendEvent {
    fun invokeEvent(message: ChatMessage): ActionResult

    companion object {
        val logger = LoggerUtil.get("chat", "sending")
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
                    ClientNetworkUtil.sendMessage(
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