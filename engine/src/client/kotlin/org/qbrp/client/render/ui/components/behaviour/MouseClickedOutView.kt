package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.ui.components.event.MouseClick
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class MouseClickedOutView(ctx: ViewContext) : ViewComponent(ctx), CloseTrigger, MouseClick {
    override var shouldClose: Boolean = false
    override fun onClick(lmX: Float, lmY: Float, button: Int): Boolean {
        if (lmX < 0 || lmX > view.width || lmY < 0 || lmY > view.height) shouldClose = true
        return false
    }
}