package org.qbrp.main.engine.players.model.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readProtobuf
import org.qbrp.main.core.networking.writeProtobuf
import org.qbrp.main.engine.players.model.mapping.PlayerListDto

class PlayerListPacket(val playerListDto: PlayerListDto): Packet<PlayerPacketListener> {
    constructor(buf: PacketByteBuf) : this(buf.readProtobuf(PlayerListDto.serializer()))

    override fun write(buf: PacketByteBuf) {
        buf.writeProtobuf(playerListDto, PlayerListDto.serializer())
    }

    override fun apply(listener: PlayerPacketListener) {
        listener.onPlayerList(playerListDto)
    }
}