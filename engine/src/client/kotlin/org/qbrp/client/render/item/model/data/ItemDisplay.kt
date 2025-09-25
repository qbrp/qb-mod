package org.qbrp.client.render.item.model.data

import org.qbrp.client.deprecated.TooltipDataAdapter
import org.qbrp.main.engine.items.ModelId
import org.qbrp.main.engine.text.components.TextComponent

interface ItemDisplay {
    val model: ModelId
    val name: TextComponent
    val tooltip: ItemTooltip?
}