package org.qbrp.main.core.networking.synchronization

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.networking.readDtos
import org.qbrp.main.core.networking.writeDtos

open class DtoSynchronizePacket<O: Identifiable>(override val id: String,
                                                 val dto: List<DtoTask>) : SynchronizePacket<O>() {
    constructor(buf: PacketByteBuf) : this(
        buf.readString(),
        buf.readDtos<DTO>()
    )

    override fun write(buf: PacketByteBuf) {
        super.write(buf)
        buf.writeDtos(dto)
    }
}
