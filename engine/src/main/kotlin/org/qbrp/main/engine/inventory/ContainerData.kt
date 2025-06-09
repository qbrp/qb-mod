package org.qbrp.main.engine.inventory

import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.model.ItemRepository

data class ContainerData(val ids: MutableList<String> = mutableListOf<String>(),
                         val inventory: MutableList<InventoryEntry> = mutableListOf()
) {
    fun load(itemRepository: ItemRepository, module: ItemsModule, notFound: () -> Unit) {
        ids.forEach { id ->
            itemRepository.getByIdOrLoad(
                id,
                module,
                { item -> inventory.add(item) },
                notFound
            )
        }
    }
}