package org.qbrp.main.engine.tooltip.component

import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.text.style.Style

data class PlainTextTooltipComponent(val lines: List<TextComponent>): TooltipComponent {
    companion object {
        fun of(literal: String, style: Style = Style.DEFAULT) = PlainTextTooltipComponent(listOf(literal(literal, style)))
    }
}