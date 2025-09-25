package org.qbrp.client.render.ui.components.texture

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class Background(ctx: ViewContext, var color: Int, var padding: Float = 0f, val texture: Boolean = false): ViewComponent(ctx), Drawable {
    override fun onRender(ctx: Draw2dContext) {
        if (!texture) ctx.fill(
            0 + padding,
            0 + padding,
            view.width - padding,
            view.height - padding,
            color
        )
        else ctx.fillWhiteTexture(
            0 + padding,
            0 + padding,
            view.width - padding,
            view.height - padding,)
    }
}