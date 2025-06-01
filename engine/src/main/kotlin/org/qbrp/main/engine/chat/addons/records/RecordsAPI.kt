package org.qbrp.main.engine.chat.addons.records

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.modules.ModuleAPI
import kotlin.collections.set

interface RecordsAPI: ModuleAPI {
    fun addMessage(message: ChatMessage)
}