package org.qbrp.main.engine.items.model

import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec3d
import org.koin.core.component.get
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.items.ItemObject
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.synchronization.components.LocalPlayerMessageSender
import org.qbrp.main.engine.synchronization.components.ObjectMessageSender
import org.qbrp.main.engine.synchronization.position.SquaredRadiusSynchronizable
import org.qbrp.main.engine.synchronization.state.ObjectSynchronizable

class ServerItemObject(
    override val id: String,
    override val state: State,
    private val module: ItemsModule,
    type: String = "abstract_item",
    override val messageSender: ObjectMessageSender = module.get(),
) : ItemObject(type), SquaredRadiusSynchronizable, LocalPlayerMessageSender {

    override val pos: Vec3d = entity?.pos ?: holder?.pos ?: Vec3d.ZERO
    override val syncDistance: Int = 10

    fun copyItemStack(): ItemStack = module.copyItemStack(this)
    fun give(playerObject: PlayerObject) = playerObject.entity.giveItemStack(copyItemStack())

    override fun getCluster(): Cluster {
        return ClusterBuilder().build()
    }
}
