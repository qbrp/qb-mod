package org.qbrp.client.deprecated

import net.minecraft.client.item.TooltipData
import org.qbrp.main.engine.tooltip.component.TooltipComponent

class TooltipDataAdapter(
    val components: List<TooltipComponent>
) : TooltipData