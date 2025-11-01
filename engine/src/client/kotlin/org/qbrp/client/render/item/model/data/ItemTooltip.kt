package org.qbrp.client.render.item.model.data

import org.qbrp.main.engine.tooltip.component.TooltipComponent
import org.qbrp.main.engine.tooltip.TooltipViewData

data class ItemTooltip(override val components: List<TooltipComponent>): TooltipViewData