package org.qbrp.main.core.util.networking.messaging

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.util.networking.messages.Message
import org.qbrp.main.core.util.networking.messages.types.Signal

interface NetworkMessageSender {
    fun sendMessage(message: Message, target: ServerPlayerEntity? = null)
    fun sendSignal(target: ServerPlayerEntity, name: String) {
        sendMessage(Message(name, Signal()), target)
    }
}