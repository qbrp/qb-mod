package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.ui.components.event.Clickable
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class OnClick(ctx: ViewContext, val handle: ClickListener, val continueHande: Boolean = false): ViewComponent(ctx), Clickable {
    override fun onClick(button: Int, lx: Float, ly: Float): Boolean = handle.onClicked(view, button, vec2(lx, ly))
        .let { if (!continueHande) false else it }
}