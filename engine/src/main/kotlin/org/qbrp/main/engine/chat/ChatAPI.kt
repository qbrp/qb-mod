package org.qbrp.main.engine.chat

import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.core.modules.ModuleAPI
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface ChatAPI: ModuleAPI {
    fun sendMessage(player: ServerPlayerObject, message: ChatMessage)
    fun sendMessage(player: ServerPlayerObject, message: String, authorName: String = SYSTEM_MESSAGE_AUTHOR)
    fun createSender(): MessageSender
    fun loadAddon(addon: ChatAddon)
    fun handleMessage(message: ChatMessage)
}