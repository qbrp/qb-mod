package org.qbrp.engine.chat.addons.placeholders

import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.ModuleAPI

interface PlaceholdersAPI: ModuleAPI {
    fun handle(message: ChatMessage, filter: (String) -> Boolean = { true })
}