package org.qbrp.main.core.fabricmc

import net.minecraft.item.Item
import net.minecraft.item.Item.Settings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import org.qbrp.main.core.Core

val ABSTRACT_ITEM_SETTINGS: Settings = Settings().maxCount(1)

class AbstractItem() : Item(ABSTRACT_ITEM_SETTINGS)

// Registration

val UNDEFINED_ITEM = ItemRegistration("undefined", AbstractItem())

fun registerItems() {
    registerItem(UNDEFINED_ITEM)
}

fun registerItem(item: ItemRegistration) {
    Registry.register(Registries.ITEM, item.key, item.type)
}

fun getItem(registeredItem: ItemRegistration) = Registries.ITEM.get(registeredItem.key)

class ItemRegistration(
    val name: String,
    val type: Item,
) {
    val identifier: Identifier = Identifier(Core.MOD_ID, name)
    val key: RegistryKey<Item> = RegistryKey.of(RegistryKeys.ITEM, identifier)
}
