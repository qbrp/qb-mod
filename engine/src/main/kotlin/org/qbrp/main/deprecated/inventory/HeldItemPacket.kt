package org.qbrp.main.deprecated.inventory

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet

//class HeldItemPacket(val item: ContainerEntry?): Packet<HeldItemListener> {
////    constructor(buf: PacketByteBuf, storage: ItemStorage<out ItemObject>): this(
////        buf.readString()?.let { storage.getById(it) }
////    )
//    override fun write(buf: PacketByteBuf) {
//        if (item != null) buf.writeString(item.id)
//    }
//
//    override fun apply(listener: HeldItemListener) {
//        listener.onHeldItem(item)
//    }
//}