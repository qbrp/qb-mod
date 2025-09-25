package org.qbrp.main.engine.controls.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.engine.controls.entity.interactions.Intent

class InteractionManagerSyncPacket(val intent: Intent, val alternate: Boolean) : Packet<ClientInteractionListener> {
    constructor(buf: PacketByteBuf) : this(
        buf.readEnumConstant(Intent::class.java),
        buf.readBoolean()
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeEnumConstant(intent)
        buf.writeBoolean(alternate)
    }

    override fun apply(listener: ClientInteractionListener) {
        listener.onSync(intent, alternate)
    }
}