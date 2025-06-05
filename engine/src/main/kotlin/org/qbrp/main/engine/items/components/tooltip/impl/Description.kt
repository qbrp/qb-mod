package org.qbrp.main.engine.items.components.tooltip.impl

import kotlinx.serialization.Serializable
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.items.components.tooltip.TooltipData
import org.qbrp.main.engine.synchronization.`interface`.SynchronizeConvertible

@Serializable
/**
 * Статическое описание, отображаемое для всех игроков одинаково
 * В будущем сделать РП-описанием
 *  **/
class Description(val text: String): TooltipData(), SynchronizeConvertible {
    override fun toCluster(player: PlayerObject): Cluster {
        return ClusterBuilder().component("text", text).build()
    }
}