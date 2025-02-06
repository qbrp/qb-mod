package org.qbrp.engine.client.render.hud.chat

import net.minecraft.client.gui.DrawContext
import org.qbrp.engine.client.engine.chat.system.Typer

interface ChatHudElement {
    fun render(drawContext: DrawContext, tickDelta: Float, context: Typer.TypingMessageContext)
}