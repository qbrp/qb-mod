package org.qbrp.client.render.ui.components.tooltip

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.mechanics.tooltip.TooltipColorProfile
import org.qbrp.main.mechanics.tooltip.component.TooltipComponent
import org.qbrp.main.mechanics.tooltip.TooltipViewData
import org.qbrp.client.render.tooltip.render.MainTooltipRenderer
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.LayoutElement
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.utils.sumOf

class Tooltip(ctx: ViewContext,
              var tooltip: TooltipViewData,
              var color: TooltipColorProfile,
              private val tooltipRenderer: MainTooltipRenderer): ViewComponent(ctx), Drawable, LayoutElement {
    private var measures: Map<TooltipComponent, Size> = mutableMapOf()

    override fun measure(c: Constraints): Size {
        measures = tooltip.components.associateWith { tooltipRenderer.measure(it) }
        val maxWidth = measures.values.maxOf { it.w }.coerceIn(c.minW, c.maxW)
        val h = measures.values.sumOf { it.h }.coerceIn(c.minH, c.maxH)
        return Size(maxWidth, h)
    }

    override fun onRender(ctx: Draw2dContext) {
        var accumulatedY = 0f
        tooltip.components.forEach {
            tooltipRenderer.draw(it, vec2(0f, accumulatedY), ctx, this)
            accumulatedY += measures[it]!!.h
        }
    }
}