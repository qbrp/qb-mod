package org.qbrp.engine.chat.core.messages

import net.minecraft.text.Text

interface ChatMessageData {
    fun getVanillaText(): Text
}