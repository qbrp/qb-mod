package org.qbrp.main.engine.chat.addons.groups

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.main.core.modules.ModuleAPI

interface ChatGroupsAPI: ModuleAPI {
    fun addGroup(group: ChatGroup)
    fun fetchGroup(message: ChatMessage): ChatGroup?
    fun getGroup(name: String): ChatGroup?
    fun getStorage(): ChatGroupsStorage
}