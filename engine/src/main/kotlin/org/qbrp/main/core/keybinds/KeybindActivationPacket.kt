package org.qbrp.main.core.keybinds

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.engine.players.model.PlayerObject

class KeybindActivationPacket(val id: KeybindId, val player: PlayerObject? = null): Packet<KeybindPacketListener> {
    constructor(buf: PacketByteBuf, player: PlayerObject) : this(
        KeybindId(buf.readString()),
        player
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeString(id.value)
    }

    override fun apply(listener: KeybindPacketListener) {
        listener.onKeybind(player!!, id)
    }
}