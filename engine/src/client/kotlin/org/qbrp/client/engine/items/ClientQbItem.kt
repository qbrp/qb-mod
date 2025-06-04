package org.qbrp.client.engine.items

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import org.koin.core.component.KoinComponent
import org.qbrp.client.engine.ClientEngine
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.components.Tooltip
import org.qbrp.main.engine.items.model.ItemStorage
import org.qbrp.main.engine.items.model.ServerItemObject

class ClientQbItem(): Item(ItemsModule.SETTINGS), KoinComponent {
    companion object {
        val items by lazy { ClientEngine.getModule<ClientItems>() }
        val storage by lazy { items.getLocal<ItemStorage<ClientItemObject>>() }
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        storage.getObject(stack)?.let {
            val tooltipComponent = it.getComponent<Tooltip>() ?: return
            tooltip.addAll(tooltipComponent.provide(stack, world, context))
        }
    }
}