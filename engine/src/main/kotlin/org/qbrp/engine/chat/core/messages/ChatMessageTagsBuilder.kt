package org.qbrp.engine.chat.core.messages

import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.types.StringContent

class ChatMessageTagsBuilder: ClusterBuilder() {

    override fun toString(): String {
        return "${components}"
    }

    fun placeholder(name: String, value: String, ephemeral: Boolean = false): ChatMessageTagsBuilder {
        component("value.$name${if (ephemeral) ".ephemeral" else ""}", StringContent(value))
        return this
    }

    override fun build(): ChatMessageTagsCluster {
        return ChatMessageTagsCluster(components)
    }

}