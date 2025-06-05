package org.qbrp.main.core.game.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.saving.AutomaticSaver
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.game.serialization.ContextualFactory
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.game.serialization.Serializer
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.items.ItemsModule

open class LifecycleManager<T : BaseObject, C>(
    open val storage: Storage<T>,
    open val saver: Saver<T>
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
            scope.launch { saver.saveObject(obj) }
        }
    }
}