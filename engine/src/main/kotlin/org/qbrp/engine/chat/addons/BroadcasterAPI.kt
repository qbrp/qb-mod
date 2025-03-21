package org.qbrp.engine.chat.addons

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.ModuleAPI

interface BroadcasterAPI: ModuleAPI {
    fun broadcastGlobalDo(message: ChatMessage)
    fun broadcast(message: ChatMessage, targets: List<ServerPlayerEntity>)
    fun broadcast(message: ChatMessage)
    fun broadcast(text: String)
}