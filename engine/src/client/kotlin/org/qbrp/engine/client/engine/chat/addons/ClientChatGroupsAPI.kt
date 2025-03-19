package org.qbrp.engine.client.engine.chat.addons

import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer

interface ClientChatGroupsAPI: ModuleAPI {
    fun loadChatGroups(groups: List<ChatGroup>)
    fun getChatGroups(): ChatGroupsStorage
    fun createChatGroupFromCluster(cluster: ClusterViewer): ChatGroup
}

