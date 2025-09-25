package org.qbrp.main.core.mc.player.permission

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readStringList
import org.qbrp.main.core.networking.writeStringList
import org.qbrp.main.engine.players.model.networking.packet.PlayerPermissions

class PermissionsPacket(val permissions: PlayerPermissions) : Packet<PermissionListener> {
    constructor(buf: PacketByteBuf) : this(PlayerPermissions(buf.readStringList().map { Permission(it) }, buf.readBoolean()),)

    override fun write(buf: PacketByteBuf) {
        buf.writeStringList(permissions.map { it.name })
        buf.writeBoolean(permissions.isOp)
    }

    override fun apply(listener: PermissionListener) {
        listener.onPermissions(permissions)
    }
}