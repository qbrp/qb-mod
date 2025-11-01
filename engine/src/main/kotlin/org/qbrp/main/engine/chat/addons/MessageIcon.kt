package org.qbrp.main.engine.chat.addons

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.util.networking.messages.components.ClusterBuilder


class MessageIcon(val type: String, val color: Int, val text: String) {
    fun components(): ClusterBuilder {
        return ClusterBuilder()
            .component("icon.identity", type)
            .component("icon.color", color)
            .component("icon.chatText", text)
    }

    companion object {
        fun getMessageIcon(message: ChatMessage): MessageIcon? {
            val tags = message.getTags()
            return MessageIcon(
                tags.getComponentData<String>("icon.identity") ?: return null,
                tags.getComponentData<Int>("icon.identity") ?: return null,
                tags.getComponentData<String>("icon.identity") ?: return null
            )
        }
    }
}