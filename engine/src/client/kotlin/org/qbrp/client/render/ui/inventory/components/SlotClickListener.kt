package org.qbrp.client.render.ui.inventory.components

interface SlotClickListener {
    fun onSlotClick(slot: Slot)
    fun onSlotRightClick(slot: Slot)
}