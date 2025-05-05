package org.qbrp.core.mc.items

import org.qbrp.core.mc.items.groups.ItemGroups
import org.qbrp.core.resources.structure.integrated.Parents
import org.qbrp.engine.items.QbItem
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
