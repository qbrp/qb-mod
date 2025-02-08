package org.qbrp.engine.chat.core.messages

import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.types.StringContent

class ChatMessageTagsBuilder: ClusterBuilder() {

    fun placeholder(name: String, value: String): ChatMessageTagsBuilder {
        component("value.$name", StringContent(value))
        return this
    }

    override fun build(): ChatMessageTagsCluster {
        return ChatMessageTagsCluster(components)
    }

}