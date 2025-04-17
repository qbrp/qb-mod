package org.qbrp.engine.client.render.hud

import icyllis.modernui.widget.TextView
import net.minecraft.client.gui.tooltip.Tooltip

class TextTooltipComponent(tooltipView: TooltipView, val defaultText: String, val size: TooltipTextSize = TooltipTextSize.TEXT): TextView(tooltipView.context) {
    init {
        text = defaultText
        setTextColor(0xFFFFFFFF.toInt())
        textSize = size.value * tooltipView.textSizeMultiplier
    }

}