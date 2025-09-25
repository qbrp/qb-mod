package org.qbrp.client.deprecated

import net.minecraft.item.ItemStack

interface HeldItem {
    fun getStack(): ItemStack
    fun setItem(item: ItemStack?)
    val isEmpty get() = getStack().isEmpty
}