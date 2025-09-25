package org.qbrp.client.deprecated

import net.minecraft.item.ItemStack

class HeldItemManager: HeldItem {
    var heldItem: ItemStack? = null
    override fun getStack(): ItemStack {
        return heldItem ?: ItemStack.EMPTY
    }

    override fun setItem(item: ItemStack?) {
        this.heldItem = item
    }
}
