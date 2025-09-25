package org.qbrp.client.render.ui.inventory.components

import org.qbrp.client.engine.interactions.service.ClientInteractionManager

class ExecuteInteractionClickListener(private val interactionManager: ClientInteractionManager): SlotClickListener {
    override fun onSlotClick(slot: Slot) {}

    override fun onSlotRightClick(slot: Slot) {
        interactionManager.call()
    }
}