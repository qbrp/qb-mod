package org.qbrp.main.engine.controls.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.engine.players.model.ServerPlayerObject

class NoClipPacket(val enabled: Boolean, val player: ServerPlayerObject? = null) : Packet<ServerInteractionListener> {
    constructor(buf: PacketByteBuf, player: ServerPlayerObject) : this(buf.readBoolean(), player)

    override fun write(buf: PacketByteBuf) {
        buf.writeBoolean(enabled)
    }

    override fun apply(listener: ServerInteractionListener) {
        listener.onNoClip(player!!, enabled)
    }
}