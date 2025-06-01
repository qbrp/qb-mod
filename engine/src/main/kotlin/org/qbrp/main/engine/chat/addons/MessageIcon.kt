package org.qbrp.main.engine.chat.addons

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.Component


class MessageIcon(val type: String, val color: Int, val text: String) {
    fun components(): ClusterBuilder {
        return ClusterBuilder()
            .component("icon.type", type)
            .component("icon.color", color)
            .component("icon.text", text)
    }

    companion object {
        fun getMessageIcon(message: ChatMessage): MessageIcon? {
            val tags = message.getTags()
            return MessageIcon(
                tags.getComponentData<String>("icon.type") ?: return null,
                tags.getComponentData<Int>("icon.type") ?: return null,
                tags.getComponentData<String>("icon.type") ?: return null
            )
        }
    }
}