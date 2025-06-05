package org.qbrp.main.engine.synchronization.components

import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

interface InternalMessageBroadcaster {
    fun broadcastMessage(id: String, content: ClusterViewer)
}