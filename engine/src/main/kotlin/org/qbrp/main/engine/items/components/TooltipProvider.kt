package org.qbrp.main.engine.items.components

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.world.World

interface TooltipProvider {
    fun provide(stack: ItemStack, world: World?, context: TooltipContext): List<net.minecraft.text.Text>
}