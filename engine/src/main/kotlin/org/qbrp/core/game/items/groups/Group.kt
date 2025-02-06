package org.qbrp.core.game.items.groups

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registries.ITEM_GROUP
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.qbrp.core.Core

class Group(
    val name: String,
    val displayKey: String, // Ключ для перевода
    val icon: ItemStack,
    val builder: (Group) -> Unit
) {
    private val identifier: RegistryKey<ItemGroup> = RegistryKey.of(ITEM_GROUP.getKey(), Identifier.of(Core.MOD_ID, name));
    private val group = FabricItemGroup.builder()
        .displayName(Text.literal(displayKey))
        .icon { icon }
        .build()

    fun addItems(item: List<Item>) {
        ItemGroupEvents.modifyEntriesEvent(identifier).register { content ->
            item.forEach { content.add(it) }
        }
    }

    init {
        Registry.register(ITEM_GROUP, identifier, group)
        apply { builder(this) }
    }
}