package org.qbrp.main.engine.players.model.networking.packet

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.networking.PlayerPacketListener

class PlayerInstantiatePacket(val entityName: Username, val session: SessionData? = null) : Packet<PlayerPacketListener> {
    constructor(buf: PacketByteBuf, session: SessionData): this(
        Username(buf.readString()),
        session
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeString(entityName.value)
    }

    override fun apply(listener: PlayerPacketListener) {
        listener.onLocalPlayerInstantiatedOnServer(session!!, entityName.value)
    }
}

data class PlayerPermissions(val permissions: List<Permission>, val isOp: Boolean) : Iterable<Permission> by permissions {
    fun hasPermission(perm: Permission) = permissions.contains(perm) || isOp

    companion object {
        val EMPTY = PlayerPermissions(listOf(), false)
    }
}