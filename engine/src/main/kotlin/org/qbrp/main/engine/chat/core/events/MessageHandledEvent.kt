package org.qbrp.main.engine.chat.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.core.messages.ChatMessage


fun interface MessageHandledEvent {
    fun onMessageSend(message: ChatMessage, receivers: List<ServerPlayerEntity>): ActionResult

    companion object {
        val EVENT: Event<MessageHandledEvent> = EventFactory.createArrayBacked(
            MessageHandledEvent::class.java,
            { listeners: Array<out MessageHandledEvent> ->
                MessageHandledEvent { message, receivers ->
                    for (listener in listeners) {
                        val result = listener.onMessageSend(message, receivers)
                        if (result != ActionResult.PASS) {
                            return@MessageHandledEvent result
                        }
                    }
                    ActionResult.SUCCESS
                }
            }
        )
    }
}