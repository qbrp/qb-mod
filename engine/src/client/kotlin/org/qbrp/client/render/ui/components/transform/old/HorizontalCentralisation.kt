package org.qbrp.client.render.ui.components.transform.old

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class HorizontalCentralisation(ctx: ViewContext, val offset: Int = 0) : ViewComponent(ctx) {
    override fun onTransform(ctx: Draw2dContext) {
        view.setPos(x = ((view.parent.width - view.width) / 2) + offset)
    }
}