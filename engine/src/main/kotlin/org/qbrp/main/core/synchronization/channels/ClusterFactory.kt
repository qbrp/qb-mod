package org.qbrp.main.core.synchronization.channels

import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

fun interface ClusterFactory<T> {
    fun create(viewer: ClusterViewer, id: String): T
}