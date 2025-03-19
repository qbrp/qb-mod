package org.qbrp.engine.chat.core.messages

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.resources.units.Unit
import org.qbrp.engine.chat.core.events.MessageHandledEvent
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.system.ServerChatNetworking
import kotlin.text.toMutableList

class ShadowSender(private val networking: ServerChatNetworking, private val targets: MutableList<ServerPlayerEntity>) : MessageSender(networking, targets) {
    override fun send(message: ChatMessage) {
        targets.forEach { networking.sendMessagePacket(it, message) }
    }
}