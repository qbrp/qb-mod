package org.qbrp.main.core.utils.networking.messages.types.json

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.utils.networking.messages.types.BilateralContent

class JsonContent : BilateralContent() {
    var json: JsonObject = JsonObject()

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.writeString(json.toString())
    }

    override fun setData(data: Any) {
        json = data as JsonObject
    }

    override fun convert(buf: PacketByteBuf): JsonContent {
        val jsonString = buf.readString() // Читаем JSON из буфера
        json = JsonParser.parseString(jsonString).asJsonObject.also { json = it }
        return this
    }

    override fun getData(): Any {
        return json
    }
}