package org.qbrp.engine.client.render.hud.elements

import net.minecraft.client.gui.DrawContext

interface HudElement {
    fun render(drawContext: DrawContext, tickDelta: Float)
}