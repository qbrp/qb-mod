package org.qbrp.main.engine.text.style

import org.qbrp.main.engine.Colors

data class Style(val color: TextColor, val shadow: Boolean = false) {
    companion object {
        val DEFAULT = Style(TextColor(Colors.WHITE), false)
        fun ofColor(color: TextColor) = Style(color)
    }
}