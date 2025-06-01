package org.qbrp.main.engine.chat.addons.placeholders

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.modules.ModuleAPI

interface PlaceholdersAPI: ModuleAPI {
    fun handle(message: ChatMessage, filter: (String) -> Boolean = { true })
}