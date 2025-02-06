package org.qbrp.engine.chat.system

import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.chat.messages.ChatMessageTagsBuilder
import org.qbrp.engine.chat.messages.ChatMessageTagsCluster
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer

abstract class ChatNetworking {
    fun getChatMessage(message: Message): ChatMessage {
        val cluster = message.getContent<ClusterViewer>()
        return ChatMessage(
            authorName = cluster.getComponentData<String>("authorName")!!,
            text = cluster.getComponentData<String>("text")!!,
            metaTags = cluster.getCluster<Cluster>("tags")!!.getBuilder() as ChatMessageTagsBuilder,
            uuid = cluster.getComponentData<String>("uuid")!!,
        )
    }
}