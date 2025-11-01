package org.qbrp.client.render.ui.inventory.components

import org.qbrp.client.controls.ClientInteractionManager

class ExecuteInteractionClickListener(private val interactionManager: ClientInteractionManager): SlotClickListener {
    override fun onSlotClick(slot: Slot) {}

    override fun onSlotRightClick(slot: Slot) {
        interactionManager.call()
    }
}