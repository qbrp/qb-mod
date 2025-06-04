package org.qbrp.client.engine.items.components.tooltip.mc

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.text.Text

class StaticTooltipRender(
    private val lines: List<Text>,
    private val lineSpacing: Int = 2
) : TooltipComponent {

    override fun getHeight(): Int {
        return lines.size * 11 + (lines.size - 1).coerceAtLeast(0) * lineSpacing
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        var maxWidth = 0
        for (line in lines) {
            val w = textRenderer.getWidth(line)
            if (w > maxWidth) maxWidth = w
        }
        return maxWidth
    }

    override fun drawItems(textRenderer: TextRenderer, x: Int, y: Int, context: DrawContext) {
        var currentY = y
        for ((index, line) in lines.withIndex()) {
            context.drawText(textRenderer, line, x, currentY, 0xFFFFFFFF.toInt(), false)
            currentY += 11
            if (index < lines.lastIndex) {
                currentY += lineSpacing
            }
        }
    }
}