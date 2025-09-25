package org.qbrp.client.render.ui.components.tooltip

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.mechanics.tooltip.TooltipColorProfile
import org.qbrp.client.render.tooltip.render.TooltipBackgroundRenderer
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.require

class TooltipBackground(ctx: ViewContext,
                        val colorProfile: TooltipColorProfile,
                        private val renderer: TooltipBackgroundRenderer): ViewComponent(ctx), Drawable {
    private val transform = view.require<RectTransform>()
    private val pos = vec2(0f, 0f)

    override fun onRender(ctx: Draw2dContext) {
        renderer.renderTooltipBackground(
            ctx,
            colorProfile,
            pos,
            0f,
            Size(view.width, view.height)
        )
    }
}