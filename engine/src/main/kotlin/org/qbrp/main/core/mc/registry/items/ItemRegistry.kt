package org.qbrp.main.core.mc.registry.items

import org.qbrp.main.core.mc.registry.groups.InventoryGroupsRegistry
import org.qbrp.deprecated.resources.structure.integrated.Parents
import org.qbrp.main.engine.items.QbItem
import org.qbrp.main.core.utils.log.LoggerUtil

class ItemRegistry() {
    val logger = LoggerUtil.get("registries", "items")
    val baseItems = mutableListOf<ItemDefinition>()
    val itemGroups = InventoryGroupsRegistry()

    fun getItem(name: String): ItemDefinition {
        return baseItems.find { it.name == name } ?: throw IllegalArgumentException("$name not found")
    }

    fun registerItem(item: ItemDefinition) {
        baseItems.add(item)
        logger.log("Registered item ${item.name}")
    }
}
