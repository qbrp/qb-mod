package org.qbrp.main.engine.chat.addons

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.modules.ModuleAPI

interface BroadcasterAPI: ModuleAPI {
    fun broadcastGlobalDo(message: ChatMessage)
    fun broadcast(message: ChatMessage, targets: List<ServerPlayerEntity>)
    fun broadcast(message: ChatMessage)
    fun broadcast(text: String)
}