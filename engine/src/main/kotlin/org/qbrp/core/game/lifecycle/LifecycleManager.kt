package org.qbrp.core.game.lifecycle

import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.model.storage.Storage
import org.qbrp.core.game.serialization.SerializeFabric

open class LifecycleManager<T : BaseObject>(
    protected open val storage: Storage<Long, T>,
    protected open val db: ObjectDatabaseService,
    protected open val fabric: SerializeFabric<T, *>
): Lifecycle<T> {

    open override fun onCreated(obj: T) {
        storage.add(obj)
        obj.state.putObject(obj)
    }

    open override fun unload(obj: T) {
        save(obj)
        if (!obj.eternal) {
            obj.state.removeAllComponents()
            storage.remove(obj.id)
        }
    }

    open override fun save(obj: T) {
        if (!obj.ephemeral) db.saveObject(fabric.toJson(obj))
    }
}