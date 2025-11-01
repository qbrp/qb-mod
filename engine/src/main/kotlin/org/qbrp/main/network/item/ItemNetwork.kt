package org.qbrp.main.network.item

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.chemistry.params.PhysicalParameters
import org.qbrp.main.engine.items.components.meta.ItemLocation
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.players.inventory.ItemListInstance
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.items.display.components.ItemDisplay
import org.qbrp.main.engine.items.model.ItemId
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.props.components.SpawnedProp
import org.qbrp.main.network.physics.toDto
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

data class ItemNetFrame(
    val items: List<ServerItemObject>,
    val players: List<ServerPlayerObject>
)

fun runItemSynchronizationThread(
    netFrames: ConcurrentLinkedQueue<ItemNetFrame>,
    distance: Double = 32.0,
) {
    thread(name = "Engine Item Synchronization", isDaemon = true) {
        while (true) {
            while (netFrames.isNotEmpty()) {
                try {
                    val frame = netFrames.poll()

                    for (player in frame.players) {
                        sendNearestItemsPackets(player, frame.items, distance)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Thread.sleep(1)
        }
    }
}

fun sendNearestItemsPackets(
    player: ServerPlayerObject,
    items: List<ServerItemObject>,
    distance: Double = 32.0
) {
    for (item in items) {
        val isItemListInstance = item.has<ItemListInstance>()

        if (!isItemListInstance) {
            val location = item.get<ItemLocation>() ?: continue
            if (location.pos.asVec3().squaredDistanceTo(player.pos) > distance * distance) continue
        }

        sendItemPackets(player, item)
    }
}

@JvmName("sendNearestItemPacketsExtension")
fun ServerPlayerObject.sendNearestItemsPackets(
    items: List<ServerItemObject>,
    distance: Double = 32.0
) {
    sendNearestItemsPackets(this, items, distance)
}


fun sendItemPackets(player: ServerPlayerObject, item: ServerItemObject) {
    val display = item.get<ItemDisplay>()
    display?.sendPacket(player)
    item.sendItemPhysicsStatePacket(player)
}

fun ServerItemObject.sendItemPhysicsStatePacket(player: PlayerReference) {
    val dimensions = get<Dimensions>()
    val prop = get<SpawnedProp>()?.let { PropData(it.body.id, it.body.shape.toDto()) }
    val physicalParameters = get<PhysicalParameters>()
    if (dimensions == null) return
    val packet = ItemPhysicalParametersPacket(ItemId(id), dimensions, prop, physicalParameters?.toDto())

    ITEM_PHYSICAL_PARAMETERS_ENDPOINT
        .sendS2C(packet, player)
}