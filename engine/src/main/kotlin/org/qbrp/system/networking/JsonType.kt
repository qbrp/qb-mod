package org.qbrp.system.networking

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf

data class JsonType(val json: JsonObject): MessageType {

    override fun writeByteBuf(): PacketByteBuf {
        val buf = PacketByteBufs.create()
        buf.writeString(json.toString())
        return buf
    }

    override fun convertByteBuf(buf: PacketByteBuf): JsonObject {
        val jsonString = buf.readString()
        return JsonParser.parseString(jsonString).asJsonObject
    }
}
