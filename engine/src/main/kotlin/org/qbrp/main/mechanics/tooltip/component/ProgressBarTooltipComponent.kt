package org.qbrp.main.mechanics.tooltip.component

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.text.components.TextComponent

data class ProgressBarTooltipComponent(val progress: Float,
                                       val progressBarWidth: Float,
                                       val colorProfile: ColorProfile,
                                       val annotation: TextComponent): TooltipComponent {
    init { require(progress <= 1f && progress >= 0f) }

    @Serializable
    data class ColorProfile(val startColor: Int, val endColor: Int) {
        companion object {
            val GREEN_RED = ColorProfile(Colors.GREEN, Colors.RED)
        }
    }
}