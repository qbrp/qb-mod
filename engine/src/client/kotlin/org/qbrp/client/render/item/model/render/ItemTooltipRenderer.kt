package org.qbrp.client.render.item.model.render

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.item.model.data.ItemDisplay

interface ItemTooltipRenderer {
    fun renderTooltip(display: ItemDisplay, pos: Vec2)
}