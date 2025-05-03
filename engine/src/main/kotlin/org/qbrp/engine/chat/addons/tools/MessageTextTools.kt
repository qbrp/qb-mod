package org.qbrp.engine.chat.addons.tools

import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.networking.messages.types.StringContent

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