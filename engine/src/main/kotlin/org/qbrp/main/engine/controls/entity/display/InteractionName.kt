package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.engine.text.components.LiteralTextComponent
import org.qbrp.main.engine.text.components.TextComponent

@JvmInline
value class InteractionName<T: TextComponent>(val text: T): TextComponent by text {
    companion object {
        fun literal(string: String) = InteractionName(LiteralTextComponent(string))
    }
}