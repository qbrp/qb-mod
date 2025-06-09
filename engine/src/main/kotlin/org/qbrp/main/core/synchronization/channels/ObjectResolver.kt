package org.qbrp.main.core.synchronization.channels

import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

fun interface ObjectResolver<T: Identifiable> {
    @Throws(Exception::class)
    fun resolve(viewer: ClusterViewer, id: String): T?
}
