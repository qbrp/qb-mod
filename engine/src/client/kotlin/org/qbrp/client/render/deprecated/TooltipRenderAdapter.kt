package org.qbrp.client.render.deprecated.mc

import net.minecraft.client.font.TextRenderer as McTextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent as McTooltipComponent
import org.qbrp.client.deprecated.TooltipDataAdapter
import org.qbrp.client.render.screen.context.DrawContextAdapterFactory
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.tooltip.render.MainTooltipRenderer

class TooltipRenderAdapter(val container: TooltipDataAdapter,
                           private val textRenderer: TextRenderer,
                           private val renderer: MainTooltipRenderer,
                           private val drawContextAdapterFactory: DrawContextAdapterFactory
): McTooltipComponent {
    private val children get() = container.components

    override fun getHeight(): Int {
//        var total = 0
//        for (child in children) {
//            total += renderer.measure(child, textRenderer).h.toInt()
//        }
//        return total
        return 0
    }

    override fun getWidth(textRenderer: McTextRenderer): Int {
//        var maxW = 0
//        for (child in children) {
//            val w = renderer.measure(child, this.textRenderer).w.toInt()
//            if (w > maxW) maxW = w
//        }
//        return maxW
        return 0
    }

    override fun drawItems(textRenderer: McTextRenderer, x: Int, y: Int, context: DrawContext) {
//        var offsetY = 0
//        for (child in children) {
//            renderer.draw(child, this.textRenderer, vec2(x, y), drawContextAdapterFactory.adapt(context))
//            offsetY += renderer.measure(child, this.textRenderer).h.toInt()
//        }
    }
}