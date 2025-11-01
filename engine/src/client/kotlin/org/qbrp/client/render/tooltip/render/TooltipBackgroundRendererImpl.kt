package org.qbrp.client.render.tooltip.render

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.engine.tooltip.TooltipColorProfile
import org.qbrp.client.render.ui.components.layout.Size
import kotlin.math.ceil
import kotlin.math.floor

class TooltipBackgroundRendererImpl() : TooltipBackgroundRenderer {

    override fun renderTooltipBackground(
        ctx: Draw2dContext,
        colorProfile: TooltipColorProfile,
        pos: Vec2,
        z: Float,
        size: Size
    ) {
        render(ctx, colorProfile, pos.x, pos.y, z, size.w.toInt(), size.h.toInt())
    }

    private fun render(ctx: Draw2dContext, colorProfile: TooltipColorProfile, x: Float, y: Float, z: Float, width: Int, height: Int) {
        val pad = 3f

        val left   = floor(x - pad)
        val top    = floor(y - pad)
        val right  = ceil(x + width + pad)
        val bottom = ceil(y + height + pad)

        val w = (right - left).toInt()
        val h = (bottom - top).toInt()

        renderHorizontalLine(ctx, z,left, top - 1, w, colorProfile.background)
        renderHorizontalLine(ctx, z,left, top + h, w, colorProfile.background)
        renderRectangle(ctx, z - 0.001f, left, top, w, h, colorProfile.background)
        renderVerticalLine(ctx, z, left - 1, top, h, colorProfile.background)
        renderVerticalLine(ctx, z, left + w, top, h, colorProfile.background)

        renderBorder(ctx, z, left, top + 1, w, h, colorProfile.borderStartY, colorProfile.borderEndY)
    }

    private fun renderBorder(
        ctx: Draw2dContext, z: Float, x: Float, y: Float, width: Int, height: Int,
        startColor: Int, endColor: Int
    ) {
        renderVerticalLine(ctx, z, x, y, height - 2, startColor, endColor)
        renderVerticalLine(ctx, z, x + width - 1, y, height - 2, startColor, endColor)
        renderHorizontalLine(ctx, z, x, y - 1, width, startColor)
        renderHorizontalLine(ctx, z, x, y - 1 + height - 1, width, endColor)
    }

    private fun renderVerticalLine(ctx: Draw2dContext, z: Float, x: Float, y: Float, height: Int, color: Int) {
        ctx.fill(x, y, x + 1, y + height, z, color)
    }

    private fun renderVerticalLine(ctx: Draw2dContext, z: Float, x: Float, y: Float, height: Int, startColor: Int, endColor: Int) {
        ctx.fillGradientVertical(x, y, x + 1, y + height, z, startColor, endColor)
    }

    private fun renderHorizontalLine(ctx: Draw2dContext, z: Float, x: Float, y: Float, width: Int, color: Int) {
        ctx.fill(x, y, x + width, y + 1, z, color)
    }

    private fun renderRectangle(ctx: Draw2dContext, z: Float, x: Float, y: Float, width: Int, height: Int, color: Int) {
        ctx.fill(x, y, x + width, y + height, z, color)
    }
}