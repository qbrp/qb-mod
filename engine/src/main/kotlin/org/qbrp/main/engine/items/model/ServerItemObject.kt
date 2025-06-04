package org.qbrp.main.engine.items.model


import net.minecraft.item.ItemStack
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.items.ItemObject
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.synchronization.`interface`.state.ObjectSynchronizable

class ServerItemObject(
    private val module: ItemsModule,
    type: String = "abstract_item",
) : ItemObject(type), ObjectSynchronizable {

    override fun shouldSync(playerObject: PlayerObject) = true

    fun copyItemStack(): ItemStack = module.copyItemStack(this)
    fun give(playerObject: PlayerObject) = playerObject.entity.giveItemStack(copyItemStack())

    override fun getCluster(): Cluster {
        return ClusterBuilder()
            .component("id", id)
            .component("tooltip", "Test Tooltip")
            .build()
    }
}
