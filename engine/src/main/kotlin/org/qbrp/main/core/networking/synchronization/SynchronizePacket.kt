package org.qbrp.main.core.networking.synchronization

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.networking.Packet

abstract class SynchronizePacket<O: Identifiable>(): Packet<Resolver<O>> {
    abstract val id: String

    override fun write(buf: PacketByteBuf) {
        buf.writeString(id)
    }

    override fun apply(listener: Resolver<O>) {
        listener.resolve(this)
        true
    }
}