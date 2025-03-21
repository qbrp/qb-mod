package org.qbrp.core.game.items

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier
import org.qbrp.core.Core.Companion.MOD_ID
import org.qbrp.core.resources.structure.integrated.Parents

class BaseItem(
    val name: String,
    val type: Item = BaseItemType(Item.Settings()),
    val modelType: Parents = Parents.GENERATED
) {
    val identifier: Identifier = Identifier(MOD_ID, name)
    val key: RegistryKey<Item> = RegistryKey.of(RegistryKeys.ITEM, identifier)

    init {
        Registry.register(Registries.ITEM, key, type)
    }

    fun get() = Registries.ITEM.get(key)
}
