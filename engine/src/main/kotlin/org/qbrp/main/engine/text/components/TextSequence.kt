package org.qbrp.main.engine.text.components

import org.qbrp.main.engine.players.model.context.PlayerContext
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.components.TextComponent

data class TextSequence(override val style: Style, val children: List<TextComponent>) : TextComponent, TextContent {
    override fun transform(ctx: PlayerContext): TextComponent {
        return TextSequence(style, children.map { it.transform(ctx) })
    }

    override val text: String
        get() = children.filterIsInstance<TextContent>().joinToString { it.text }

    class Builder() {
        private var components: MutableList<TextComponent> = mutableListOf()
        private var style: Style? = null
        fun add(component: TextComponent): Builder {
            components += component
            return this
        }
        fun literal(text: String, style: Style = Style.Companion.DEFAULT): Builder {
            add(LiteralTextComponent(text, style))
            return this
        }

        fun miniMessage(text: String) {
            add(MiniMessageTextComponent(text))
        }

        fun rootStyle(style: Style): Builder {
            this.style = style
            return this
        }

        fun build(): TextSequence {
            return TextSequence(style ?: Style.Companion.DEFAULT, components)
        }

        operator fun String.unaryPlus() {
            literal(this)
        }
    }
}