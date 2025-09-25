package org.qbrp.client.render.ui.components.layout.grid

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.View

interface LineDrawer {
    fun drawHorizontalLine(x1: Float, x2: Float, y: Float, thick: Float, ctx: Draw2dContext, view: View)
    fun drawVerticalLine(y1: Float, y2: Float, x: Float, thick: Float, ctx: Draw2dContext, view: View)
}