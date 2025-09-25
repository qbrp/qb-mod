package org.qbrp.client.render.ui.components.layout.grid

import org.qbrp.client.render.ui.components.event.Clickable
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class OnRowClick(ctx: ViewContext, val clickListener: GridRowClickListener, private val autoGridLayout: AutoGridLayout, val continueHandle: Boolean = false): ViewComponent(ctx), Clickable {
    override fun onClick(button: Int, lx: Float, ly: Float): Boolean {
        val b = autoGridLayout.elemAt(lx, ly)?.let { clickListener.onRowClicked(button, lx, ly, autoGridLayout, it) } ?: false
       return if (!continueHandle) b else false
    }
}

fun interface GridRowClickListener {
    fun onRowClicked(button: Int, lx: Float, ly: Float, layout: AutoGridLayout, elem: AutoGridLayout.Elem): Boolean
}