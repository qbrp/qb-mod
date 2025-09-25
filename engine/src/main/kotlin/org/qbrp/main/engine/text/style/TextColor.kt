package org.qbrp.main.engine.text.style

import org.qbrp.main.engine.Colors

@JvmInline
value class TextColor(val hex: Int) {
    companion object {
        val GRAY = TextColor(Colors.GRAY)
        val RED = TextColor(16733525)
        val AQUA = TextColor(5636095)
        val GREEN = TextColor(5635925)
    }
}