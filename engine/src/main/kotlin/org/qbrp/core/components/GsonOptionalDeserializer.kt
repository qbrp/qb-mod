package org.qbrp.core.components

import com.google.gson.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.Optional

class GsonOptionalDeserializer<T> : JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Optional<T> {
        val asJsonArray = json.asJsonArray
        val jsonElement = asJsonArray[0]
        val value = context.deserialize<T>(jsonElement, (typeOfT as ParameterizedType).actualTypeArguments[0])
        return Optional.ofNullable(value) as Optional<T>
    }

    override fun serialize(src: Optional<T>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = context.serialize(src.orElse(null))
        val result = JsonArray()
        result.add(element)
        return result
    }
}
