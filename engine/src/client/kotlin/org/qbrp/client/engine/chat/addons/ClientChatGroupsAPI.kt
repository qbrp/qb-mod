package org.qbrp.client.engine.chat.addons

import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.main.core.modules.ModuleAPI

interface ClientChatGroupsAPI: ModuleAPI {
    fun loadChatGroups(groups: List<ChatGroup>)
    fun getChatGroups(): ChatGroupsStorage
}

