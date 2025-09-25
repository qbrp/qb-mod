package org.qbrp.client.render.ui.inventory.components

import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.tooltip.TooltipManager

class ShowTooltipSlotHoverListener(private val uiContext: UiContext, private val tooltipManager: TooltipManager): SlotHoverListener {
    override fun onHover(slot: Slot): Unit = with(uiContext) {
        slot.item?.display?.tooltip?.let { tooltipManager.showTooltipOrUpdatePos(it) }
    }

    override fun onSlotHoverStart(slot: Slot): Unit = with(uiContext) {
        slot.item?.display?.tooltip?.let { tooltipManager.showTooltipOrUpdatePos(it) }
    }

    override fun onSlotHoverEnd(slot: Slot) {
        tooltipManager.hideTooltip()
    }
}