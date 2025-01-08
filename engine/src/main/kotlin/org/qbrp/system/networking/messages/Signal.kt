package org.qbrp.system.networking.messages

import com.google.gson.JsonObject
import net.minecraft.network.PacketByteBuf

class Signal: MessageContent() {
    override fun write(buf: PacketByteBuf) { }
    override fun convert(buf: PacketByteBuf): Signal { return this }
}