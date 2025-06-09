package org.qbrp.main.engine.items.model

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack

interface PropTickHandler {
    fun tick(itemStack: ItemStack, entity: Entity?)
}