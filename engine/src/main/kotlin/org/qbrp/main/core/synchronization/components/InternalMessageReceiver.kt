package org.qbrp.main.core.synchronization.components

import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

interface InternalMessageReceiver {
    fun onMessage(id: String, content: ClusterViewer) {}
    fun onMessage(id: String, playerObject: ServerPlayerObject, content: ClusterViewer) {}
}