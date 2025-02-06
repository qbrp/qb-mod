package org.qbrp.core.game.items

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.qbrp.core.Core.Companion.MOD_ID
import org.qbrp.core.game.items.groups.ItemGroups
import org.qbrp.system.utils.log.Loggers

class Items() {
    val logger = Loggers.get("items", "mechanics")
    val baseItems = mutableListOf<BaseItem>()
    val itemGroups = ItemGroups()

    fun getBaseItem(name: String): BaseItem? {
        return baseItems.find { it.name == name } ?: run { logger.error("Базовый предмет $name не найден"); throw Exception() }
    }

    fun registerItems(item: List<BaseItem>) {
        item.forEach { registerItem(it) }
    }

    fun registerItem(item: BaseItem) {
        baseItems.add(item)
        Registry.register(Registries.ITEM, Identifier(MOD_ID, item.name), item.type)
        logger.log("Registered item ${item.name}")
    }
}
