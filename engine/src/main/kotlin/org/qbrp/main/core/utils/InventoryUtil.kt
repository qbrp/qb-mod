package org.qbrp.main.core.utils

import net.minecraft.entity.Entity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import org.qbrp.main.engine.items.model.ItemTicker.ItemWithContext

object InventoryUtil {
    fun extractItemsStacks(inventory: Inventory): List<ItemStack> {
        return (0 until inventory.size()).map { i -> inventory.getStack(i) }
    }
    private fun Inventory.extractItemStacks(): List<ItemStack> = extractItemsStacks(this)
}