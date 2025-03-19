package org.qbrp.engine.chat.core.messages

import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.types.StringContent

class ChatMessageTagsBuilder(): ClusterBuilder() {

    override fun toString(): String {
        return "${components}"
    }

    override fun override(enabled: Boolean): ChatMessageTagsBuilder {
        super.override(enabled)
        return this
    }

    fun placeholder(name: String, value: String, ephemeral: Boolean = false): ChatMessageTagsBuilder {
        component("value.$name${if (ephemeral) ".ephemeral" else ""}", StringContent(value))
        return this
    }

    fun textComponent(name: String, value: String, msg: ChatMessage, modify: (String, String) -> String): ChatMessageTagsBuilder {
        component(name, StringContent(value))
        MessageTextTools.setTextContent(msg, modify(MessageTextTools.getTextContent(msg), """<$name:"$value">"""))
        return this
    }

    override fun copy(): ChatMessageTagsBuilder {
        val copiedBuilder = ChatMessageTagsBuilder()
        copiedBuilder.components.addAll(this.components.map { it.copy() })
        return copiedBuilder
    }

    override fun build(): ChatMessageTagsCluster {
        return ChatMessageTagsCluster(components)
    }

}