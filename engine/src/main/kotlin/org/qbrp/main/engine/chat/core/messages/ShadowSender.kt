package org.qbrp.main.engine.chat.core.messages

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.engine.chat.core.system.ServerChatNetworking

class ShadowSender(private val networking: ServerChatNetworking, private val targets: MutableList<ServerPlayerEntity>) : MessageSender(networking, targets) {
    override fun send(message: ChatMessage) {
        targets.forEach { networking.sendMessagePacket(it, message) }
    }
}