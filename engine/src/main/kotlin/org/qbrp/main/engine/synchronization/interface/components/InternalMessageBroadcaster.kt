package org.qbrp.main.engine.synchronization.`interface`.components

import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.types.ReceiveContent

interface InternalMessageBroadcaster {
    fun broadcastMessage(id: String, content: ClusterViewer)
}