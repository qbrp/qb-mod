package org.qbrp.main.engine.items.model

import kotlinx.coroutines.launch
import org.qbrp.main.core.game.lifecycle.LifecycleManager
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.synchronization.`interface`.Synchronizer
import org.qbrp.main.engine.items.ItemsModule

class ItemLifecycle(override val storage: ItemStorage<ServerItemObject>,
                    override val table: TableAccess,
                    override val fabric: ItemFabric,
                    val module: ItemsModule,
                    val networking: Synchronizer,
): LifecycleManager<ServerItemObject>(storage, table, fabric) {

    override fun unload(obj: ServerItemObject) {
        super.unload(obj)
        scope.launch {
            table.saveObject(obj, fabric.toJson(obj))
        }
    }

}