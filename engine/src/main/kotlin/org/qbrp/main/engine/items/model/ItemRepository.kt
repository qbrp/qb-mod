package org.qbrp.main.engine.items.model

import net.minecraft.item.ItemStack
import org.qbrp.main.core.game.storage.TableRepository
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.items.ItemsModule

class ItemRepository(val module: ItemsModule, table: TableAccess, factory: ItemFabric, storage: ItemStorage<ServerItemObject>
): TableRepository<ServerItemObject, ItemsModule>(table, factory, storage) {
    fun getItemObjectOrLoad(stack: ItemStack, onLoad: (ServerItemObject) -> Unit, notFound: () -> Unit = {}) {
        stack.nbt?.getString("id")?.let { id ->
            getByIdOrLoad(id, module, onLoad, notFound)
        } ?: notFound()
    }
}