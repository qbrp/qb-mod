package org.qbrp.main.engine.controls.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readProtobuf
import org.qbrp.main.core.networking.writeProtobuf
import org.qbrp.main.engine.controls.dto.InteractionContextMenuViewDto

class InteractionListPacket(val contextMenuDto: InteractionContextMenuViewDto): Packet<ClientInteractionListener> {
    constructor(buf: PacketByteBuf): this(
        buf.readProtobuf<InteractionContextMenuViewDto>()
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeProtobuf(contextMenuDto, InteractionContextMenuViewDto.serializer())
    }

    override fun apply(listener: ClientInteractionListener) {
        listener.onInteractionList(contextMenuDto)
    }
}