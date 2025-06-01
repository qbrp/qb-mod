package org.qbrp.main.engine.items.model

import kotlinx.coroutines.launch
import org.qbrp.main.core.game.lifecycle.LifecycleManager
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.items.ItemsModule

class ItemLifecycle(override val storage: ItemStorage,
                    override val table: TableAccess,
                    override val fabric: ItemFabric,
                    val module: ItemsModule
): LifecycleManager<ItemObject>(storage, table, fabric) {

    override fun onCreated(obj: ItemObject) {
        super.onCreated(obj)
        obj.owner?.let {
            obj.give(it)
        }
    }

    override fun unload(obj: ItemObject) {
        super.unload(obj)
        scope.launch {
            table.saveObject(obj, fabric.toJson(obj))
        }
    }

}