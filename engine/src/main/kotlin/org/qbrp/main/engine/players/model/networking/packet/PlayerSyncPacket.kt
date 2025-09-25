package org.qbrp.main.engine.players.model.networking.packet

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.networking.synchronization.SynchronizePacket
import org.qbrp.main.engine.players.model.PlayerObject

open class PlayerSyncPacket(override val id: String, val entityName: Username) : SynchronizePacket<PlayerObject>() {
    constructor(buf: PacketByteBuf) : this(
        buf.readString(),
        Username(buf.readString())
    )
    constructor(player: PlayerObject) : this(player.id, player.username)

    override fun write(buf: PacketByteBuf) {
        super.write(buf)
        buf.writeString(entityName.value)
    }
}