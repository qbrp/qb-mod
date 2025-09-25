package org.qbrp.main.core.mc.server.command

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet

class CommandExecutePacket(val command: RawMinecraftCommand): Packet<PlayerCommandExecuteListener> {
    constructor(buf: PacketByteBuf): this(RawMinecraftCommand(buf.readString()))
    override fun write(buf: PacketByteBuf) {
        buf.writeString(command.value)
    }

    override fun apply(listener: PlayerCommandExecuteListener) {
        listener.onCommand(command)
    }
}