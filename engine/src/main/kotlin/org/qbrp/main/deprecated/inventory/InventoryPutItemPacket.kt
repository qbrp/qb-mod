package org.qbrp.main.deprecated.inventory

//import net.minecraft.network.PacketByteBuf
//import org.qbrp.main.core.networking.PacketListener
//import org.qbrp.main.engine.players.model.PlayerObject
//
//class InventoryPutItemPacket(itemId: String, containerId: String, slot: Int, player: PlayerObject? = null): InventoryPacket(itemId, containerId, slot, player) {
//    constructor(buf: PacketByteBuf, player: PlayerObject) : this(
//        buf.readString(),
//        buf.readString(),
//        buf.readInt(),
//        player
//    )
//
//    override fun apply(listener: PacketListener) {
//        TODO("Not yet implemented")
//    }
//}