package org.qbrp.engine.client.render.hud

import net.minecraft.client.gui.DrawContext

interface HudElement {
    fun render(drawContext: DrawContext, tickDelta: Float)
}