package org.qbrp.client.engine.items.components.tooltip

import net.minecraft.item.ItemStack
import net.minecraft.text.Text

interface TextNameProvider {
    fun provideName(itemStack: ItemStack): Text
}