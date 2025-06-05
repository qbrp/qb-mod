package org.qbrp.client.core.synchronization

import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

fun interface ClusterFactory<T> {
    fun create(viewer: ClusterViewer, id: String): T
}