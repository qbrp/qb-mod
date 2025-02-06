package org.qbrp.engine.chat.messages

import net.minecraft.server.network.ServerPlayerEntity

interface Sender {
    fun send(message: ChatMessage)
}