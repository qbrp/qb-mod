package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.event.Hoverable
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class OnHover(ctx: ViewContext, val listener: HoverListener): ViewComponent(ctx), Drawable, Hoverable {
    private var hover: Boolean = false

    override fun onRender(ctx: Draw2dContext) {
        if (!hover) { listener.onNotHover(ctx, view) }
        listener.render(ctx, view)
    }

    override fun onHover(x: Float, y: Float) {
        listener.onHover(view, this)
    }

    override fun onHoverEnter() {
        hover = true
        listener.onHoverStart(this)
    }

    override fun onHoverExit() {
        hover = false
        listener.onHoverEnd(this)
    }
}