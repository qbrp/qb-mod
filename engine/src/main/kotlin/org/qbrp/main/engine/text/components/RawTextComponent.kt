package org.qbrp.main.engine.text.components

import net.minecraft.text.OrderedText
import net.minecraft.text.Text

data class RawTextComponent(val mcText: Text): TextComponent, TextContent {
    override val text: String get() = mcText.string
}