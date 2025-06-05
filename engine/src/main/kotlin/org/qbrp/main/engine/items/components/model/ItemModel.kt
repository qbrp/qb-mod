package org.qbrp.main.engine.items.components.model

import kotlinx.serialization.Serializable
import net.minecraft.item.ItemStack
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.items.components.ItemTick

@Serializable
class ItemModel(var model: String): ItemBehaviour(), ItemTick {
    companion object {
        const val NBT_KEY = "QbrpModel"
    }
    override fun inventoryTick(itemStack: ItemStack) {
        itemStack.nbt!!.putString(NBT_KEY, model)
    }
}