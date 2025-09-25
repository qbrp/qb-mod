package org.qbrp.client.render.ui.components.layout.grid

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.require

class GridLines(ctx: ViewContext, val lineDrawer: LineDrawer): ViewComponent(ctx), Drawable {
    private val grid: AutoGridLayout by lazy { view.require() }
    override fun onRender(ctx: Draw2dContext) {
        val colGap = grid.colGap
        val rowGap = grid.rowGap
        val colX: List<Float> = grid.colW.runningFold(0f) { acc, w -> acc + w + colGap }
        val rowY: List<Float> = grid.rowH.runningFold(0f) { acc, h -> acc + h + rowGap }

        colX.forEachIndexed { index, it ->
            lineDrawer.drawVerticalLine(0f, view.height, it - if (index != 0) colGap / 2f else 0f, 1f, ctx, view)
        }
        rowY.forEachIndexed { index, it ->
            lineDrawer.drawHorizontalLine(0f, view.width, it - if (index != 0) rowGap / 2f else 0f, 1f, ctx, view)
        }
    }
}