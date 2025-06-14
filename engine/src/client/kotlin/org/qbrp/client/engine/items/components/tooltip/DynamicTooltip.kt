package org.qbrp.client.engine.items.components.tooltip

import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.item.TooltipData
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.items.components.tooltip.impl.ItemDisplay

abstract class DynamicTooltip: ItemBehaviour(), TooltipData, TooltipComponent {
    protected lateinit var tooltipManager: ItemDisplay
    override fun onEnable() {
        tooltipManager = requireState().getComponentOrAdd { ItemDisplay() }
    }
}