package org.qbrp.main.engine.chat.core.messages

import net.minecraft.text.Text
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR

class VanillaChatMessage(authorName: String, private val vanillaText: Text): ChatMessage(authorName, vanillaText.string) {
    override fun getVanillaText(): Text = vanillaText

    companion object {
        fun create(text: Text, author: String = SYSTEM_MESSAGE_AUTHOR): ChatMessage {
            return VanillaChatMessage(author, text)
        }
    }
}