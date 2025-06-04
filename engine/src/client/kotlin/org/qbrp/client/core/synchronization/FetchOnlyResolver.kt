package org.qbrp.client.core.synchronization

import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.ObjectProvider
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

class FetchOnlyResolver<T: Identifiable>(
    private val storage: ObjectProvider<T>
) : ObjectResolver<T> {
    override fun resolve(viewer: ClusterViewer, id: String): T {
        return storage.getById(id)
            ?: throw IllegalStateException("Object with id=$id not found")
    }
}