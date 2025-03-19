package org.qbrp.engine.chat.core.messages

import icyllis.modernui.text.SpannableString
import net.minecraft.text.Text

interface ChatMessageData {
    fun getTextSpans(): SpannableString
    fun getVanillaText(): Text
}