package org.qbrp.client.engine.items

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import org.koin.core.component.KoinComponent
import org.qbrp.client.engine.ClientEngine
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.mc.registry.items.AbstractItem
import org.qbrp.main.core.mc.text.MinecraftTextMapper
import org.qbrp.main.engine.items.service.ItemStorage

class ClientQbItem(): Item(AbstractItem.SETTINGS), KoinComponent {
    companion object {
        val items by lazy { ClientEngine.getModule<ClientItemsModule>() }
        val storage by lazy { items.getLocal<ItemStorage<ClientItemObject>>() }
    }

    override fun getName(stack: ItemStack): Text =
        storage.getItemObject(stack)
            ?.get<ItemDisplay>()?.name
            ?.let { MinecraftTextMapper.fromEngineText(it) }
        ?: Text.of("Загрузка имени...")
}