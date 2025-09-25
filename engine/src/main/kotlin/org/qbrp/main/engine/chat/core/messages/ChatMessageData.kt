package org.qbrp.main.engine.chat.core.messages

import net.minecraft.text.Text

interface ChatMessageData {
    fun getVanillaText(): Text
}