package org.qbrp.system.networking.messages.types.json

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import net.minecraft.network.PacketByteBuf
import org.qbrp.system.networking.messages.types.BilateralContent

class JsonArrayContent : BilateralContent() {
    var array: JsonArray = JsonArray()

    override fun toString(): String {
        return try {
            array.asString
        } catch (e: Exception) {
            ""
        }
    }

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.writeString(array.toString())
    }

    override fun setData(data: Any) {
        array = data as JsonArray
    }

    override fun convert(buf: PacketByteBuf): JsonArrayContent {
        val jsonString = buf.readString()  // Читаем JSON строку из буфера
        array = JsonParser.parseString(jsonString).asJsonArray.also { array = it }
        return this
    }

    override fun getData(): Any {
        return array
    }
}