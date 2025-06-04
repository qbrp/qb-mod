package org.qbrp.main.engine.items.components.tooltip

import kotlinx.serialization.Serializable
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.synchronization.`interface`.SynchronizeConvertible

@Serializable
class Description(val text: String): TooltipData(), SynchronizeConvertible {
    override fun toCluster(player: PlayerObject): Cluster {
        return ClusterBuilder().component("text", text).build()
    }
}