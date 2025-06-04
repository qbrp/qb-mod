package org.qbrp.client.engine.items.components.tooltip

import net.minecraft.client.item.TooltipData
import net.minecraft.item.ItemStack

interface TooltipProvider {
    fun provideTooltip(stack: ItemStack): TooltipData?
}