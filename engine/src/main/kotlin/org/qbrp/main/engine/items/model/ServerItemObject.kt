package org.qbrp.main.engine.items.model

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.koin.core.component.get
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.engine.items.components.containers.Dimensions
import org.qbrp.main.engine.items.components.containers.VolumeContainable
import org.qbrp.main.engine.items.ItemObject
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.items.components.physics.Physics
import org.qbrp.main.core.synchronization.components.S2CMessaging
import org.qbrp.main.core.synchronization.components.MessagingChannelSender

class ServerItemObject(
    override val id: String,
    override val state: State,
    val module: ItemsModule,
    type: String = "abstract_item",
    override val messageSender: MessagingChannelSender = module.get(),
) : ItemObject(type), ItemSync, S2CMessaging, VolumeContainable {

    override val weightGrams get() = getComponent<Physics>()!!.weightGrams
    override val dimensions: Dimensions get() = getComponent<Physics>()!!.dimensions
    override val volume: Double get() = getComponent<Physics>()!!.volume

    override val syncDistance: Int = 10
    override val pos: Vec3d get() = entity?.pos ?: holder?.pos ?: Vec3d.ZERO

    fun give(playerObject: ServerPlayerObject) = playerObject.entity.giveItemStack(copyItemStack())
    fun handleUse(stack: ItemStack, world: World, player: PlayerEntity) {
        state.getComponentsIsInstance<ItemBehaviour>().forEach { it.onUse(stack, world, player) }
    }
}
