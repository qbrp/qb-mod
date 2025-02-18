package org.qbrp.engine.chat.addons

import net.minecraft.server.network.ServerPlayerEntity

interface ChatBroadcaster {
    fun broadcast(message: String)
    fun broadcast(message: String, targets: List<ServerPlayerEntity>)
}