package org.qbrp.main.engine.inventory

import net.minecraft.item.ItemStack

interface Stackable {
    fun asItemStack(): ItemStack
}