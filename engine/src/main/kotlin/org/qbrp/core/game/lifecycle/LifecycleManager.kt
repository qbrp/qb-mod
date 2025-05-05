package org.qbrp.core.game.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.model.storage.Storage
import org.qbrp.core.game.serialization.SerializeFabric

open class LifecycleManager<T : BaseObject>(
    open val storage: Storage<Long, T>,
    open val db: ObjectDatabaseService,
    open val fabric: SerializeFabric<T, *>
): Lifecycle<T> {
    protected val scope = CoroutineScope(Dispatchers.IO)

    open override fun onCreated(obj: T) {
        storage.add(obj)
        obj.state.putObjectAndEnableBehaviours(obj)
    }

    open override fun unload(obj: T) {
        save(obj)
        if (!obj.eternal) {
            obj.state.removeAllComponents()
            storage.remove(obj.id)
        }
    }

    open override fun save(obj: T) {
        if (!obj.ephemeral) {
            scope.launch {
                db.saveObject(fabric.toJson(obj))
            }
        }
    }
}