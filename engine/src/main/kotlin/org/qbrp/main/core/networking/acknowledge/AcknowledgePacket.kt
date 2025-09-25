package org.qbrp.main.core.networking.acknowledge

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet

class AcknowledgePacket(private val id: Long): Packet<AcknowledgePacketListener> {
    constructor(buf: PacketByteBuf): this(buf.readLong())
    override fun write(buf: PacketByteBuf) {
        buf.writeLong(id)
    }

    override fun apply(listener: AcknowledgePacketListener) {
        listener.onAcknowledge(id)
    }
}