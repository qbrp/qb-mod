package org.qbrp.main.core.networking.require

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet

class RequirePacket(val requestType: RequireId, val requestId: RequestId): Packet<RequirePacketListener> {
    constructor(buf: PacketByteBuf) : this(RequireId(buf.readString()), RequestId(buf.readInt()))
    override fun write(buf: PacketByteBuf) {
        buf.writeString(requestType.value)
        buf.writeInt(requestId.value)
    }

    override fun apply(listener: RequirePacketListener) {
        listener.onRequire(requestType, requestId)
    }
}