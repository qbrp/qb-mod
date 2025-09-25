package org.qbrp.client.render.ui.components.transform.old

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class VerticalCentralisation(ctx: ViewContext, val offset: Int = 0) : ViewComponent(ctx) {
    override fun onTransform(ctx: Draw2dContext) {
        view.setPos(y = ((view.parent.height - view.height) / 2) + offset)
    }
}