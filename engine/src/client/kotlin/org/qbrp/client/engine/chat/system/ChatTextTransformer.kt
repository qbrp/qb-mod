package org.qbrp.client.engine.chat.system

import org.qbrp.client.engine.chat.system.events.ChatInputEditEvent

class ChatTextTransformer {
    fun transformShortcuts(text: String): String {
        return ChatInputEditEvent.EVENT.invoker().transform(text)
    }

    fun getTransformedMessage(message: String): String {
        return transformShortcuts(message)
    }
}