package org.qbrp.engine.chat.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.addons.tools.MessageTextEvents
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.chat.core.system.ServerChatNetworking

fun interface MessageSendEvent {
    fun onMessageSend(
        sender: MessageSender,
        message: ChatMessage,
        receiver: ServerPlayerEntity,
        networking: ServerChatNetworking
    ): ActionResult

    companion object {
        enum class Priority { LOWEST, LOW, NORMAL, HIGH, HIGHEST, LAST }

        private data class Entry(val listener: MessageSendEvent, val priority: Priority)
        private val entries = mutableListOf<Entry>()

        @JvmStatic
        fun register(listener: MessageSendEvent) {
            register(listener, Priority.NORMAL)
        }
        @JvmStatic
        fun register(listener: MessageSendEvent, priority: Priority) {
            entries += Entry(listener, priority)
        }

        val EVENT: MessageSendEvent = MessageSendEvent { sender, message, receiver, networking ->
            for ((listener, _) in entries.sortedBy { it.priority.ordinal }) {
                val result = listener.onMessageSend(sender, message, receiver, networking)
                if (result == ActionResult.FAIL)      return@MessageSendEvent ActionResult.FAIL
                if (result != ActionResult.PASS)      return@MessageSendEvent result
            }
            MessageTextEvents.pasteText(message)
            networking.sendMessagePacket(receiver, message)
            ActionResult.SUCCESS
        }
    }
}
