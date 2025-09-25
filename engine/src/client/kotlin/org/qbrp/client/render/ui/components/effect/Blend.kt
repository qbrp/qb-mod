package org.qbrp.client.render.ui.components.effect

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class Blend(ctx: ViewContext): ViewComponent(ctx), Drawable {
    override fun onRender(ctx: Draw2dContext) {}
    override fun initRender(ctx: Draw2dContext) {
        RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
            GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR,
            GlStateManager.SrcFactor.ONE,
            GlStateManager.DstFactor.ZERO
        )
    }

    override fun endRender(ctx: Draw2dContext) {
        RenderSystem.defaultBlendFunc()
    }
}