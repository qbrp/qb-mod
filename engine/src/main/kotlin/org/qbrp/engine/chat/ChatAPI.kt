package org.qbrp.engine.chat

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.system.modules.ModuleAPI

interface ChatAPI: ModuleAPI {
    fun sendMessage(player: ServerPlayerEntity, message: ChatMessage)
    fun sendMessage(player: ServerPlayerEntity, message: String, authorName: String = SYSTEM_MESSAGE_AUTHOR)
    fun createSender(): MessageSender
    fun loadAddon(addon: ChatAddon)
    fun handleMessage(message: ChatMessage)
}