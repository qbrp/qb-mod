package org.qbrp.main.deprecated.inventory

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.engine.inventory.networking.ContainerSyncListener
import org.qbrp.main.engine.players.model.PlayerObject

abstract class InventoryPacket(val itemId: String,
                      val containerId: String,
                      val slot: Int,
                      val player: PlayerObject?): Packet<ContainerSyncListener> {
    override fun write(buf: PacketByteBuf) {
        buf.writeString(itemId)
        buf.writeString(containerId)
        buf.writeInt(slot)
    }
}