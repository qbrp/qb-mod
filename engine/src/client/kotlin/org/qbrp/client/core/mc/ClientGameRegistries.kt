package org.qbrp.client.core.mc

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.client.engine.ClientEngine
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.client.engine.items.ClientItemsModule
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.fabricmc.ABSTRACT_ITEM_SETTINGS
import org.qbrp.main.core.fabricmc.ItemRegistration
import org.qbrp.main.core.fabricmc.UNDEFINED_ITEM
import org.qbrp.main.core.fabricmc.registerItem
import org.qbrp.main.core.fabricmc.registerItems
import org.qbrp.main.core.fabricmc.text.MinecraftTextMapper
import org.qbrp.main.engine.items.service.ItemStorage

class ClientQbItem() : Item(ABSTRACT_ITEM_SETTINGS) {
    override fun getName(stack: ItemStack): Text =
        storage.getItemObject(stack)
            ?.get<ItemDisplay>()?.name
            ?.let { MinecraftTextMapper.fromEngineText(it) }
            ?: Text.of("Загрузка имени...")

    companion object : KoinComponent {
        val storage by inject<ClientItemStorage>()
    }
}

val ABSTRACT_ITEM = ItemRegistration("abstract_item", ClientQbItem())

fun registerClientItems() {
    registerItem(UNDEFINED_ITEM)
    registerItem(ABSTRACT_ITEM)
}