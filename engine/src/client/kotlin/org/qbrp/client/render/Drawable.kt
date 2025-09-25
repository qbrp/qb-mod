package org.qbrp.client.render

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.UiContext

interface Drawable {
    fun initRender(ctx: Draw2dContext) {}
    fun onRender(ctx: Draw2dContext) {}
    fun endRender(ctx: Draw2dContext) {}
}