package org.qbrp.client.render.item.model.data

import org.qbrp.main.mechanics.tooltip.component.TooltipComponent
import org.qbrp.main.mechanics.tooltip.TooltipViewData

data class ItemTooltip(override val components: List<TooltipComponent>): TooltipViewData