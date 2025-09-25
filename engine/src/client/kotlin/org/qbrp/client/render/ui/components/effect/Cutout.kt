package org.qbrp.client.render.ui.components.effect

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.layout.Edges
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.engine.Colors

class Cutout(
    ctx: ViewContext,
    private val edgesProvider: (View) -> Edges
) : ViewComponent(ctx), Drawable {
    override fun initRender(ctx: Draw2dContext) {
        val v = view
        val e = edgesProvider(v)
        val gPos = v.globalPos
        ctx.enableScissor(gPos.x + e.l, gPos.y + e.t, gPos.x + e.r, gPos.y + e.b)
    }

    override fun endRender(ctx: Draw2dContext) {
        ctx.disableScissor()
    }
}