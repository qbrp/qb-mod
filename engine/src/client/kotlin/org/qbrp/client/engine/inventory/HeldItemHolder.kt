package org.qbrp.client.engine.inventory

import net.minecraft.item.ItemStack

class HeldItemHolder: HeldItem {
    var heldItem: ItemStack? = null
    override fun getStack(): ItemStack {
        return heldItem ?: ItemStack.EMPTY
    }

    override fun setItem(item: ItemStack?) {
        this.heldItem = item
    }
}
