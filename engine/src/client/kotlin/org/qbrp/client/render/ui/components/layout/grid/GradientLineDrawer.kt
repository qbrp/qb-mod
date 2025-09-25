package org.qbrp.client.render.ui.components.layout.grid

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.View

class GradientLineDrawer(val startColor: Int, val endColor: Int): LineDrawer {
    override fun drawHorizontalLine(
        x1: Float,
        x2: Float,
        y: Float,
        thick: Float,
        ctx: Draw2dContext,
        view: View
    ) {
        ctx.fillGradientVertical(x1, y, x2, y + thick, 0f, startColor, endColor)
    }

    override fun drawVerticalLine(
        y1: Float,
        y2: Float,
        x: Float,
        thick: Float,
        ctx: Draw2dContext,
        view: View
    ) {
        ctx.fillGradientVertical(x, y1, x + thick, y2, 0f, startColor, endColor)
    }
}