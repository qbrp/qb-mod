package org.qbrp.core.components

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class DataComponentsDeserializer : JsonDeserializer<DataComponent> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DataComponent? {
        val jsonObject = json?.asJsonObject ?: throw JsonParseException("Invalid JSON")
        val typeName = jsonObject.get("type").asString
        val dataElement = jsonObject.get("data")

        val componentClass = try {
            Class.forName(typeName)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException("Unknown component type: $typeName")
        }

        val data = context?.deserialize<Any>(dataElement, componentClass)
            ?: throw JsonParseException("Failed to deserialize data")

        return DataComponent(componentClass, data)
    }
}