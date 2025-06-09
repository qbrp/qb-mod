package org.qbrp.main.core.synchronization

import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface Synchronizer {
    fun sendMessage(playerObject: ServerPlayerObject, syncCluster: Cluster)
}