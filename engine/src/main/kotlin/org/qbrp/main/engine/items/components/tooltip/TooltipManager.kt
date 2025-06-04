package org.qbrp.main.engine.items.components.tooltip

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.anticheat.StringListContent
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.synchronization.`interface`.SynchronizeConvertible


class TooltipManager(): ItemBehaviour(), SynchronizeConvertible {
    private fun generateDescription(player: PlayerObject): List<String> {
        return requireState().getComponentsIsInstance<StaticTooltip>().map { it.provide(player, item) }
    }

    override fun toCluster(player: PlayerObject): Cluster {
        serverItem.sendMessage(Cluster(), player, "description.read")
        val lines = generateDescription(player)
        return ClusterBuilder()
            .component("lines", StringListContent().apply { setData(lines) })
            .build()
    }
}