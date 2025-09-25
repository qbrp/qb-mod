package org.qbrp.client.render.item.model.render

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.client.render.screen.DefaultZIndexes
import org.qbrp.client.render.screen.context.Draw2dContext

interface ItemRenderer {
    fun renderItem2d(ctx: Draw2dContext, item: ItemDisplay, pos: Vec2, scale: Float = 16f, z: Float = DefaultZIndexes.ITEM.value)
}