package org.qbrp.client.render.tooltip.render

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.engine.tooltip.component.TooltipComponent
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.tooltip.Tooltip

typealias MainTooltipRenderer = TooltipComponentRenderer<TooltipComponent>

interface TooltipComponentRenderer<D: TooltipComponent> {
    fun measure(data: D): Size
    fun draw(data: D, pos: Vec2, context: Draw2dContext, tooltip: Tooltip)
}