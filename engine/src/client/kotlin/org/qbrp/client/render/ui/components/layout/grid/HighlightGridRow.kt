package org.qbrp.client.render.ui.components.layout.grid

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.behaviour.Highlighter
import org.qbrp.client.render.ui.components.behaviour.HoverListener
import org.qbrp.client.render.ui.components.behaviour.ProgressMode
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent

class HighlightGridRow(params: HighlightParams, private val grid: AutoGridLayout): HoverListener {
    private val highlighter = Highlighter(params)
    private var hoveredRow: Int = 0
    private var hovered: Boolean = false

    override fun render(ctx: Draw2dContext, view: View) {
        if (!hovered) return
        val row = hoveredRow
        val size = grid.rowSize(row)
        val elem = grid.elemsAtRow(row).firstOrNull() ?: return
        highlighter.render(ctx, 0f, elem.view.y, view.width, size.h)
    }

    override fun onHover(view: View, component: ViewComponent) {
        highlighter.updateProgress(ProgressMode.INCREASE)
    }

    override fun onNotHover(ctx: Draw2dContext, view: View) {
        highlighter.updateProgress(ProgressMode.DECREASE)
    }

    override fun onHoverStart(component: ViewComponent) {
        hovered = true
    }

    override fun onHoverEnd(component: ViewComponent) {
        hovered = false
    }

    override fun handleMouseOnHover(lx: Float, ly: Float, view: View) {
        val row = grid.elemAt(lx, ly)?.gp?.row
        row?.let { hoveredRow = it }
    }
}