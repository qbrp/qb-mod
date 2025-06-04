package org.qbrp.main.engine.items.model

import net.minecraft.item.ItemStack
import org.qbrp.main.core.game.storage.GlobalStorage
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.items.ItemObject

class ItemStorage<T: ItemObject>(): GlobalStorage<T>() {
    fun getObject(stack: ItemStack): T? {
        stack.nbt?.getString("id")?.let { id ->
            return getByKey(id)
        } ?: return null
    }
}