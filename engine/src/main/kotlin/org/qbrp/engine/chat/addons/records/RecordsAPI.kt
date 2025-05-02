package org.qbrp.engine.chat.addons.records

import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.ModuleAPI
import kotlin.collections.set

interface RecordsAPI: ModuleAPI {
    fun addMessage(message: ChatMessage)
}