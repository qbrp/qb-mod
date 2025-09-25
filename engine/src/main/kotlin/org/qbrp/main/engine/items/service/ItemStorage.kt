package org.qbrp.main.engine.items.service

import net.minecraft.item.ItemStack
import org.qbrp.main.core.game.storage.GlobalStorage
import org.qbrp.main.engine.items.model.ItemObject

class ItemStorage<T: ItemObject>(): GlobalStorage<T>() {
    fun getItemObject(stack: ItemStack): T? {
        val nbtCopy = stack.nbt?.copy()
        nbtCopy?.getString("id")?.let { id ->
            return getById(id)
        } ?: return null
    }
}