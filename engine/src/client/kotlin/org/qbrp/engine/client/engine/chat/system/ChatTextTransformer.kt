package org.qbrp.engine.client.engine.chat.system

import org.qbrp.engine.client.engine.chat.system.events.ChatInputTransformEvent

class ChatTextTransformer {
    fun transformShortcuts(text: String): String {
        return ChatInputTransformEvent.EVENT.invoker().transform(text)
    }

    fun getColorTransformedMessage(message: String): String {
        val regex = Regex("(<[^>]*>?|[^<]+)")
        val result = StringBuilder()
        for (match in regex.findAll(transformShortcuts(message))) {
            val part = match.value
            if (part.startsWith("<") && part.endsWith(">")) {
                result.append(processTag(part))
            } else if (part.startsWith("<")) {
                result.append(part)
            } else {
                result.append(part)
            }
        }
        return result.toString().split("::").last()
    }

    private fun processTag(tag: String): String {
        val inner = tag.substring(1, tag.length - 1)
        val parts = inner.split(":", limit = 2)
        val formattedInner = if (parts.size == 2) {
            "&9${parts[0]}&1:&7${parts[1]}"
        } else {
            inner
        }
        return "&1<$formattedInner&1>&r"
    }
}