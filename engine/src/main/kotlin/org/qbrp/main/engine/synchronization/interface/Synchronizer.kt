package org.qbrp.main.engine.synchronization.`interface`

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface Synchronizer {
    fun sendMessage(playerObject: PlayerObject, syncCluster: Cluster)
}