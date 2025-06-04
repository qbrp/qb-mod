package org.qbrp.main.engine.items.components

import kotlinx.serialization.Serializable
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.synchronization.`interface`.state.ComponentSynchronizable

@Serializable
class Tooltip(var tooltip: String): ItemBehaviour(), ComponentSynchronizable, TooltipProvider {
    override fun toCluster(): Cluster {
        return ClusterBuilder().component("tooltip", tooltip).build()
    }
    override fun update(cluster: ClusterViewer) {
        this.tooltip = cluster.getComponentData("tooltip")!!
    }

    override fun provide(
        stack: ItemStack,
        world: World?,
        context: TooltipContext
    ): List<Text> {
        return tooltip.split("\n").map { it.asMiniMessage() }
    }
}