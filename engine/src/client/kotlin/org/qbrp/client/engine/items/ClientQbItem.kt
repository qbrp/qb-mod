package org.qbrp.client.engine.items

import net.minecraft.client.item.TooltipData
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import org.koin.core.component.KoinComponent
import org.qbrp.client.engine.ClientEngine
import org.qbrp.client.engine.items.components.tooltip.TextNameProvider
import org.qbrp.client.engine.items.components.tooltip.TooltipProvider
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.model.ItemStorage
import java.util.Optional

class ClientQbItem(): Item(ItemsModule.SETTINGS), KoinComponent {
    companion object {
        val items by lazy { ClientEngine.getModule<ClientItems>() }
        val storage by lazy { items.getLocal<ItemStorage<ClientItemObject>>() }
    }

    override fun getTooltipData(stack: ItemStack): Optional<TooltipData> {
        val itemObject = storage.getItemObject(stack)
            ?: return Optional.empty()
        val tooltipManager = itemObject.getComponent<TooltipProvider>()
            ?: return Optional.empty()
        return Optional.ofNullable(tooltipManager.provideTooltip(stack))
    }

    override fun getName(stack: ItemStack): Text = storage.getItemObject(stack)?.getComponent<TextNameProvider>()?.provideName(stack)
        ?: Text.of("Загрузка имени...")
}