package org.qbrp.client.engine.inventory.model

import net.minecraft.item.ItemStack

interface ItemProvider {
    fun provideStacks(): List<ItemStack>
}