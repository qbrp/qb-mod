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
            authorName = cluster.getComponentData("authorName")!!,
            text = cluster.getComponentData("text")!!,
            uuid = cluster.getComponentData("uuid")!!,
            timestamp = cluster.getComponentData<Long>("timestamp")!!,
        ).apply {
            setTags(cluster.getCluster<Cluster>("tags")!!.getBuilder() as ChatMessageTagsBuilder,)
            if (stripNewLines) setText(getText().replace("\n", " "))
        }
    }
}