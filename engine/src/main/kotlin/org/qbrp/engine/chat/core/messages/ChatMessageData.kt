package org.qbrp.engine.chat.core.messages

import icyllis.modernui.text.SpannableString

interface ChatMessageData {
    fun getTextSpans(): SpannableString
}