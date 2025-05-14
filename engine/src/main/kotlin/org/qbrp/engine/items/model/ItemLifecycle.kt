package org.qbrp.engine.items.model

import kotlinx.coroutines.launch
import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.game.lifecycle.LifecycleManager

class ItemLifecycle(override val storage: ItemStorage,
                    override val db: ObjectDatabaseService,
                    override val fabric: ItemFabric
): LifecycleManager<ItemObject>(storage, db, fabric) {

    override fun onCreated(obj: ItemObject) {
        super.onCreated(obj)
        obj.owner?.let {
            obj.give(it)
        }
    }

    override fun unload(obj: ItemObject) {
        super.unload(obj)
        scope.launch {
            db.archive(fabric.toJson(obj))
        }
    }

}