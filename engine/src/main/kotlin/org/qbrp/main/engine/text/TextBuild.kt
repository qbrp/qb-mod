package org.qbrp.main.engine.text

import org.qbrp.main.engine.text.components.LiteralTextComponent
import org.qbrp.main.engine.text.components.MiniMessageTextComponent
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.TextSequence
import org.qbrp.main.engine.text.components.TextSequence.Builder
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor

fun TextComponent.toSequenceBuilder(): TextSequence.Builder {
    return TextSequence.Builder()
        .add(this)
}

operator fun TextSequence.Builder.plus(component: TextComponent): Builder =
    add(component)

operator fun TextSequence.Builder.plus(text: String): Builder =
    literal(text)

fun literal(text: String, style: Style = Style.DEFAULT) = LiteralTextComponent(text, style)
fun literal(text: String, color: TextColor) = literal(text, Style(color))
fun emptyText() = LiteralTextComponent("")
fun miniMessage(text: String) = MiniMessageTextComponent(text)