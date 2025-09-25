package org.qbrp.client.render.screen.context

import net.minecraft.client.gui.DrawContext
import org.qbrp.client.render.screen.context.Draw2dContext

interface DrawContextAdapterFactory {
    fun adapt(mc: DrawContext): Draw2dContext
    fun adapt(mc: DrawContext, tickDelta: Float): Draw2dContext
}