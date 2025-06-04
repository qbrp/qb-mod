package org.qbrp.main.engine.synchronization.`interface`

import org.qbrp.main.core.mc.player.LocalPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface SynchronizeConvertible {
    fun toCluster(player: LocalPlayerObject): Cluster
    fun getName(): String = this::class.simpleName!!
}