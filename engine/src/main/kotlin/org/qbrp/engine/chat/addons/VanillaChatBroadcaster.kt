package org.qbrp.engine.chat.addons

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

interface VanillaChatBroadcaster {
    fun broadcast(message: Text)
    fun broadcast(message: Text, targets: List<ServerPlayerEntity>)
}