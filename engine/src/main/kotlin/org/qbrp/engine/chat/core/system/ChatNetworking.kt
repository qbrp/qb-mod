package org.qbrp.engine.chat.core.system

import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer

abstract class ChatNetworking {
    fun getChatMessage(message: Message, stripNewLines: Boolean = true): ChatMessage {
        val cluster = message.getContent<ClusterViewer>()
        return ChatMessage(
            authorName = cluster.getComponentData<String>("authorName")!!,
            text = cluster.getComponentData<String>("text")!!,
            metaTags = cluster.getCluster<Cluster>("tags")!!.getBuilder() as ChatMessageTagsBuilder,
            uuid = cluster.getComponentData<String>("uuid")!!,
        ).apply { if (stripNewLines) setText(getText().replace("\n", " ")) }
    }
}