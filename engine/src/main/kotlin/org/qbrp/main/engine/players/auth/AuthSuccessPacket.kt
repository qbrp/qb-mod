package org.qbrp.main.engine.players.auth

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.engine.players.model.util.PlayerId

class AuthSuccessPacket(val sessionData: SessionData): Packet<RegistrationListener> {
    constructor(buf: PacketByteBuf): this(SessionData(buf.readUuid(), PlayerId(buf.readString())))

    override fun write(buf: PacketByteBuf) {
        buf.writeUuid(sessionData.token)
        buf.writeString(sessionData.playerId.value)
    }

    override fun apply(listener: RegistrationListener) {
        listener.onAuthSuccess(sessionData)
    }
}