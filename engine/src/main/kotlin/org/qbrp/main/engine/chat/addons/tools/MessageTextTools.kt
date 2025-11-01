package org.qbrp.main.engine.chat.addons.tools

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.util.networking.messages.types.StringContent

object MessageTextTools {

    fun initializeContentMessage(message: ChatMessage) {
        message.apply {
            setTags(getTagsBuilder()
                .component("textContent", StringContent(message.getText())))
        }
    }

    fun stripBracedContent(input: String): String =
        input.replace(Regex("\\{[^}]*}"), "")

    fun getTextContent(message: ChatMessage): String {
        val text = message.getTags().getComponentData<String>("textContent")
        if (text == null) return message.getText()
        return text
    }

    fun setTextContent(message: ChatMessage, text: String) {
        val originalText = message.getTags().getComponentData<String>("textContent")
        if (originalText != null) message.apply { setTags(getTagsBuilder().component("textContent", StringContent(text))) }
        else message.setText(text)
    }

}