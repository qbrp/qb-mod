package org.qbrp.engine.client.render

import icyllis.modernui.graphics.Color
import icyllis.modernui.text.Spannable
import icyllis.modernui.text.SpannableString
import icyllis.modernui.text.style.ForegroundColorSpan
import org.qbrp.system.utils.format.ConsoleColors

object Colors {
    const val DARK_BACKGROUND = 0x40000000.toInt()
    fun rgb(r: Int, g: Int, b: Int): Int {
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
    fun rgba(r: Int, g: Int, b: Int, a: Int): Int {
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    fun black(rgb: Int): Int {
        return (0xFF shl 24) or (rgb shl 16) or (rgb shl 8) or rgb
    }

}