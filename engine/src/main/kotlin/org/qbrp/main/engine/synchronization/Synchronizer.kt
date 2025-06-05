package org.qbrp.main.engine.synchronization

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface Synchronizer {
    fun sendMessage(playerObject: PlayerObject, syncCluster: Cluster)
}