package org.qbrp.main.engine.players.auth

import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readPlayer

class AuthPacket(val password: String, val player: PlayerSnapshot? = null): Packet<AuthListener> {
    constructor(buf: PacketByteBuf, player: PlayerSnapshot) : this(
        buf.readString(),
        player)

    override fun write(buf: PacketByteBuf) {
        buf.writeString(password)
    }

    override fun apply(listener: AuthListener) {
        listener.onAuthRequest(player!!, password)
    }
}