package org.qbrp.system.networking

import com.google.gson.JsonObject
import net.minecraft.network.PacketByteBuf

interface MessageType {
    fun writeByteBuf(): PacketByteBuf
    fun convertByteBuf(buf: PacketByteBuf): JsonObject
}