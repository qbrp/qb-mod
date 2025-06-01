package org.qbrp.client.engine.chat.system.logs

import icyllis.modernui.text.SpannableString
import kotlinx.serialization.Serializable
import net.minecraft.text.Text
import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.engine.chat.core.messages.ChatMessageData
import org.qbrp.main.core.utils.format.Format.formatAsSpans
import org.qbrp.main.core.utils.format.Format.formatMinecraft

@Serializable
class ChatData(
    private val messages: MutableList<MessageDTO> = mutableListOf(),
): Asset() {
    fun getMessagesCount(): Int = messages.size
    fun getMessages(): List<MessageDTO> = messages
    fun addMessage(message: MessageDTO) = messages.add(message)

    @Serializable
    data class MessageDTO(val processedText: String, val author: String, val id: String): ChatMessageData {
        override fun getTextSpans(): SpannableString = processedText.formatAsSpans()
        override fun getVanillaText(): Text = processedText.asMiniMessage()
    }
}