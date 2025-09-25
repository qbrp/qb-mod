package org.qbrp.main.deprecated.inventory

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.engine.inventory.networking.ContainerSyncListener
import org.qbrp.main.engine.players.model.PlayerObject

class InventoryPutItemPacket(itemId: String, containerId: String, slot: Int, player: PlayerObject? = null): InventoryPacket(itemId, containerId, slot, player) {
    constructor(buf: PacketByteBuf, player: PlayerObject) : this(
        buf.readString(),
        buf.readString(),
        buf.readInt(),
        player
    )
    override fun apply(listener: ContainerSyncListener) {
//        listener.onPutItem(player as ServerPlayerObject, containerId, itemId, slot)
    }
}