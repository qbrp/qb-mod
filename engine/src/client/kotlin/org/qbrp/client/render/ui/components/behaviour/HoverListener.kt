package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent

interface HoverListener {
    fun onHoverStart(component: ViewComponent) {}
    fun onHoverEnd(component: ViewComponent) {}
    fun onNotHover(ctx: Draw2dContext, view: View) {}
    fun onHover(view: View, component: ViewComponent) {}
    fun handleMouseOnHover(lx: Float, ly: Float, view: View) {}
    fun render(ctx: Draw2dContext, view: View) {}
}