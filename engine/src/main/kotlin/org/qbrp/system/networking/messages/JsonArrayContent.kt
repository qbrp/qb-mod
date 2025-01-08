package org.qbrp.system.networking.messages

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.network.PacketByteBuf

class JsonArrayContent : MessageContent() {
    var array: JsonArray = JsonArray()

    override fun toString(): String {
        return try {
            array.asString
        } catch (e: Exception) {
            ""
        }
    }

    override fun write(buf: PacketByteBuf) {
        buf.writeString(array.toString())
    }

    override fun convert(buf: PacketByteBuf): JsonArrayContent {
        val jsonString = buf.readString()  // Читаем JSON строку из буфера
        array = JsonParser.parseString(jsonString).asJsonArray.also { array = it }
        return this
    }
}