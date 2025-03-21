package org.qbrp.core.game.items

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.qbrp.core.Core.Companion.MOD_ID
import org.qbrp.core.game.items.groups.ItemGroups
import org.qbrp.engine.items.model.QbItem
import org.qbrp.core.resources.structure.integrated.Parents
import org.qbrp.system.utils.log.Loggers

class Items() {
    val logger = Loggers.get("items", "mechanics")
    val baseItems = mutableListOf<BaseItem>()
    val itemGroups = ItemGroups()

    init {
        registerItem(BaseItem("abstract_generated", QbItem()))
        registerItem(BaseItem("abstract_handheld", QbItem(), Parents.HANDHELD))
    }

    fun getBaseItem(name: String): BaseItem? {
        return baseItems.find { it.name == name } ?: run { logger.error("Базовый предмет $name не найден"); throw Exception() }
    }

    fun registerItems(item: List<BaseItem>) {
        item.forEach { registerItem(it) }
    }

    fun registerItem(item: BaseItem) {
        baseItems.add(item)
        logger.log("Registered item ${item.name}")
    }
}
