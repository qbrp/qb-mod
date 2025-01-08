package org.qbrp.system.networking.messages
import net.minecraft.network.PacketByteBuf

class StringContent : MessageContent() {
    var string: String = ""

    override fun toString(): String = string

    override fun write(buf: PacketByteBuf) {
        buf.writeString(string)
    }

    override fun convert(buf: PacketByteBuf): StringContent {
        string = buf.readString()
        return this
    }
}