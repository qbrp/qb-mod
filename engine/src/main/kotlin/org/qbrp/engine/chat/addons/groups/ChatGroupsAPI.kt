package org.qbrp.engine.chat.addons.groups

import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.system.modules.ModuleAPI

interface ChatGroupsAPI: ModuleAPI {
    fun addGroup(group: ChatGroup)
    fun fetchGroup(message: ChatMessage): ChatGroup?
    fun getGroup(name: String): ChatGroup?
}