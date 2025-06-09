package org.qbrp.main.core.utils.networking.messaging

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.types.Signal

interface NetworkMessageSender {
    fun sendMessage(message: Message, target: ServerPlayerEntity? = null)
    fun sendSignal(target: ServerPlayerEntity, name: String) {
        sendMessage(Message(name, Signal()), target)
    }
}