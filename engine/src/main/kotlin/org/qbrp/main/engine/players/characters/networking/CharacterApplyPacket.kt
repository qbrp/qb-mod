package org.qbrp.main.engine.players.characters.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readProtobuf
import org.qbrp.main.core.networking.writeProtobuf
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.engine.players.model.PlayerObject

class CharacterApplyPacket(val character: CharacterData, val player: PlayerObject? = null): Packet<CharacterPacketListener> {
    constructor(buf: PacketByteBuf, player: PlayerObject) : this(
        buf.readProtobuf(CharacterData.serializer()),
        player
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeProtobuf(character, CharacterData.serializer())
    }

    override fun apply(listener: CharacterPacketListener) {
        listener.onCharacterApply(character, player!!)
    }
}