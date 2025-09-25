package org.qbrp.client.deprecated.hud

import org.qbrp.client.deprecated.InventoryActionHandler
import org.qbrp.client.deprecated.HeldItem
import org.qbrp.client.render.ui.inventory.components.Slot
import org.qbrp.client.render.ui.inventory.components.SlotClickListener

class SlotItemController(private val heldItem: HeldItem, private val handler: InventoryActionHandler): SlotClickListener {
    override fun onSlotClick(slot: Slot) {
//        val stack = slot.itemStack
//        when {
//            heldItem.isEmpty && !stack.isEmpty -> handler.takeItem(slot.index, slot.containerId)
//            !heldItem.isEmpty && stack.isEmpty -> handler.putItem(slot.index, slot.containerId)
//            !heldItem.isEmpty && !stack.isEmpty -> handler.swapItem(slot.index, slot.containerId)
//        }
    }

    override fun onSlotRightClick(slot: Slot) {
//        if (!heldItem.isEmpty && !slot.itemStack.isEmpty) handler.swapItem(slot.index, slot.containerId)
    }
}