package org.qbrp.client.render.ui.inventory.components

interface SlotHoverListener {
    fun onSlotHoverStart(slot: Slot)
    fun onSlotHoverEnd(slot: Slot)
    fun onHover(slot: Slot)
}