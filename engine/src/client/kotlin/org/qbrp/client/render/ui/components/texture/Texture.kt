package org.qbrp.client.render.ui.components.texture

import net.minecraft.util.Identifier
import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class Texture(ctx: ViewContext,
              val id: Identifier,
              val u: Float = 0f,
              val v: Float = 0f,
              val textureWidth: Float,
              val textureHeight: Float,
              val regionWidth: Float,
              val regionHeight: Float
): ViewComponent(ctx), Drawable {
    override fun onRender(ctx: Draw2dContext) {
        ctx.drawTexture(id,
            0f, view.width,
            0f, view.height,
            1f,
            regionWidth, regionHeight,
            u, v,
            textureWidth, textureHeight
        )
    }
}