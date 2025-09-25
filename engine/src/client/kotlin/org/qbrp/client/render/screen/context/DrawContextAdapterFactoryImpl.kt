package org.qbrp.client.render.screen.context

import net.minecraft.client.gui.DrawContext
import org.qbrp.client.render.item.impl.mc.ItemStackAdapterWrapper
import org.qbrp.client.render.item.model.render.ItemRenderer
import org.qbrp.client.render.screen.context.Draw2dContext

class DrawContextAdapterFactoryImpl(private val itemRenderer: ItemRenderer): DrawContextAdapterFactory {
    override fun adapt(mc: DrawContext): Draw2dContext {
        return Draw2dContextImpl(mc, 0.0f, itemRenderer)
    }

    override fun adapt(
        mc: DrawContext,
        tickDelta: Float
    ): Draw2dContext {
        return Draw2dContextImpl(mc, tickDelta, itemRenderer)
    }
}