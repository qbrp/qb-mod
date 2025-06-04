package org.qbrp.client.engine.items.components.tooltip.mc

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.item.TooltipData

class TooltipContainer(
    private val children: List<TooltipComponent>
) : TooltipData, TooltipComponent {
    override fun getHeight(): Int {
        var total = 0
        for (child in children) {
            total += child.height
        }
        return total
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        var maxW = 0
        for (child in children) {
            val w = child.getWidth(textRenderer)
            if (w > maxW) maxW = w
        }
        return maxW
    }

    override fun drawItems(textRenderer: TextRenderer, x: Int, y: Int, context: DrawContext) {
        var offsetY = 0
        for (child in children) {
            child.drawItems(textRenderer, x, y + offsetY, context)
            offsetY += child.height
        }
    }
}