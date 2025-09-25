package org.qbrp.main.engine.controls.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readEnum
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.players.model.ServerPlayerObject

class InteractionModeApplyPacket(val player: ServerPlayerObject?, val mode: InteractionMode) : Packet<ServerInteractionListener> {
    constructor(buf: PacketByteBuf, player: ServerPlayerObject) : this(player, buf.readEnum<InteractionMode>())

    override fun write(buf: PacketByteBuf) {
        buf.writeEnumConstant(mode)
    }

    override fun apply(listener: ServerInteractionListener) {
        listener.onInteractionMode(player!!, mode)
    }
}