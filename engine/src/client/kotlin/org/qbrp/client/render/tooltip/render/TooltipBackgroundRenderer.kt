package org.qbrp.client.render.tooltip.render

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.engine.tooltip.TooltipColorProfile
import org.qbrp.client.render.ui.components.layout.Size

interface TooltipBackgroundRenderer {
    fun renderTooltipBackground(ctx: Draw2dContext, colorProfile: TooltipColorProfile, pos: Vec2, z: Float, size: Size)
}