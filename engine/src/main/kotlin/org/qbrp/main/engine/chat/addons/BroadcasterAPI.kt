package org.qbrp.main.engine.chat.addons

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.modules.ModuleAPI
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface BroadcasterAPI: ModuleAPI {
    fun broadcastGlobalDo(message: ChatMessage)
    fun broadcast(message: ChatMessage, targets: List<ServerPlayerObject>)
    fun broadcast(message: ChatMessage)
    fun broadcast(text: String)
}