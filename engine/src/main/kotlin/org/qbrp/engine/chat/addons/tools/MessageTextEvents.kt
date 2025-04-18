package org.qbrp.engine.chat.addons.tools

import org.qbrp.engine.Engine
import org.qbrp.engine.chat.addons.placeholders.Placeholders
import org.qbrp.engine.chat.addons.placeholders.PlaceholdersAPI
import org.qbrp.engine.chat.core.messages.ChatMessage

class MessageTextEvents {
    companion object {
        fun pasteText(message: ChatMessage) {
            val textContent = message.getTags().getComponentData<String>("textContent") ?: return
            message.apply {
                setTags(
                    getTagsBuilder()
                        .placeholder("text", textContent)
                )
            }
            message.handleUpdate()
            Engine.getAPI<PlaceholdersAPI>()?.handle(message)
        }
    }
}