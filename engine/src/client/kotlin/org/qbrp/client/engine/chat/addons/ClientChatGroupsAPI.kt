package org.qbrp.client.engine.chat.addons

import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.main.core.modules.ModuleAPI
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

interface ClientChatGroupsAPI: ModuleAPI {
    fun loadChatGroups(groups: List<ChatGroup>)
    fun getChatGroups(): ChatGroupsStorage
    fun createChatGroupFromCluster(cluster: ClusterViewer): ChatGroup
}

