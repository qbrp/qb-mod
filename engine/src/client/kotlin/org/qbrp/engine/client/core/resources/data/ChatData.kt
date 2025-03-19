package org.qbrp.engine.client.core.resources.data

import icyllis.modernui.text.SpannableString
import net.minecraft.text.Text
import org.qbrp.core.resources.data.Data
import org.qbrp.engine.chat.core.messages.ChatMessageData
import org.qbrp.system.utils.format.Format.formatAsSpans
import org.qbrp.system.utils.format.Format.formatMinecraft

class ChatData(
    private val messages: MutableList<MessageDTO> = mutableListOf(),
): Data() {
    override fun toFile(): String { return gson.toJson(this) }
    fun getMessagesCount(): Int = messages.size
    fun getMessages(): List<MessageDTO> = messages
    fun addMessage(message: MessageDTO) = messages.add(message)

    data class MessageDTO(val processedText: String, val author: String, val id: String): ChatMessageData {
        override fun getTextSpans(): SpannableString = processedText.formatAsSpans()
        override fun getVanillaText(): Text = processedText.formatMinecraft()
    }
}