package org.qbrp.main.core.synchronization.channels

import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.ObjectProvider
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

class FetchOnlyResolver<T: Identifiable>(
    private val storage: ObjectProvider<T>
) : ObjectResolver<T> {
    override fun resolve(viewer: ClusterViewer, id: String): T? {
        return storage.getById(id)
    }
}