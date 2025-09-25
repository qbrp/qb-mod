package org.qbrp.main.core.mc.registry.items

import org.qbrp.main.core.utils.log.LoggerUtil

class ItemRegistry() {
    val logger = LoggerUtil.get("registries", "items")
    val baseItems = mutableListOf<ItemDefinition>()

    fun getItem(name: String): ItemDefinition {
        return baseItems.find { it.name == name } ?: throw IllegalArgumentException("$name not found")
    }

    fun registerItem(item: ItemDefinition) {
        baseItems.add(item)
        logger.log("Registered item ${item.name}")
    }
}
