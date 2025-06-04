package org.qbrp.main.engine.synchronization.`interface`.state

import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

interface SynchronizeUpdate {
    fun update(cluster: ClusterViewer)
}