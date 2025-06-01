package org.qbrp.main.core.mc.registry.items

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import org.qbrp.main.core.Core
import org.qbrp.deprecated.resources.structure.integrated.Parents

class ItemDefinition(
    val name: String,
    val type: Item,
    val modelType: Parents = Parents.GENERATED
) {
    val identifier: Identifier = Identifier(Core.MOD_ID, name)
    val key: RegistryKey<Item> = RegistryKey.of(RegistryKeys.ITEM, identifier)

    init {
        Registry.register(Registries.ITEM, key, type)
    }

    fun get() = Registries.ITEM.get(key)
}
