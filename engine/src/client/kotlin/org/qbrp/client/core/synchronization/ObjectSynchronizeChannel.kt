package org.qbrp.client.core.synchronization

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

class ObjectSynchronizeChannel<T : BaseObject>(
    name: String,
    override val resolver: SyncResolver<T>
) : ObjectProviderChannel<T>(name, resolver) {

    constructor(
        name: String,
        storage: Storage<T>,
        factory: ClusterFactory<T>
    ) : this(name, SyncResolver(storage, factory))


    fun addFabric(fabric: ComponentFabric): ObjectSynchronizeChannel<T> {
        resolver.addFabric(fabric)
        return this
    }

    fun addAssociation(old: String, new: String): ObjectSynchronizeChannel<T> {
        resolver.addAssociation(old, new)
        return this
    }

    fun shouldOverride(predicate: (T, ClusterViewer) -> Boolean): ObjectSynchronizeChannel<T> {
        resolver.shouldOverride = predicate
        return this
    }

    override fun getId(viewer: ClusterViewer): String {
        return viewer.getComponentData("id")!!
    }
}
