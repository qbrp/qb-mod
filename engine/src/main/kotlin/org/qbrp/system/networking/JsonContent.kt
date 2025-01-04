package org.qbrp.system.networking

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.network.PacketByteBuf

class JsonContent : MessageContent() {
    var json: JsonObject = JsonObject()

    override fun write(buf: PacketByteBuf) {
        buf.writeString(json.toString())
    }

    override fun convert(buf: PacketByteBuf): JsonObject {
        val jsonString = buf.readString() // Читаем JSON из буфера
        return JsonParser.parseString(jsonString).asJsonObject.also { json = it }
    }
}