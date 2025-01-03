package org.qbrp.system.networking

import com.google.gson.JsonObject
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import java.util.UUID

abstract class MessageContent {
    var messageId: String = UUID.randomUUID().toString()

    fun writeByteBuf(): PacketByteBuf {
        val buf = PacketByteBufs.create()
        buf.writeString(messageId)
        write(buf)
        return buf
    }

    fun convertByteBuf(buf: PacketByteBuf): JsonObject {
        messageId = buf.readString()
        return convert(buf)
    }

    abstract fun write(buf: PacketByteBuf)
    abstract fun convert(buf: PacketByteBuf): JsonObject
}

