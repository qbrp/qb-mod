package org.qbrp.main.engine.controls.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.controls.entity.interactions.toInteractionId
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

class InteractionSelectPacket(val interactionId: InteractionId, val player: PlayerObject? = null): Packet<ServerInteractionListener> {
    constructor(buf: PacketByteBuf, player: ServerPlayerObject) : this(
        buf.readLong().toInteractionId(),
        player
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeLong(interactionId.value)
    }

    override fun apply(listener: ServerInteractionListener) {
        listener.onSelectInteraction(player as ServerPlayerObject, interactionId)
    }
}