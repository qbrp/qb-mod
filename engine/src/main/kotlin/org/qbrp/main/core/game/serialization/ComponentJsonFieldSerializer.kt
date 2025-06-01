package org.qbrp.main.core.game.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ComponentJsonFieldSerializer : KSerializer<ComponentJsonField> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ComponentJsonField")

    override fun deserialize(decoder: Decoder): ComponentJsonField {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("ComponentJsonFieldSerializer can only be used with Json format")

        val jsonElement = jsonDecoder.decodeJsonElement()
        val jsonObject = jsonElement.jsonObject

        val type = jsonObject["type"]?.jsonPrimitive?.content
            ?: throw SerializationException("Missing 'type' field in ComponentJsonField")
        val data = jsonObject["data"]
            ?: throw SerializationException("Missing 'data' field in ComponentJsonField")

        return ComponentJsonField(type, data)
    }

    override fun serialize(encoder: Encoder, value: ComponentJsonField) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("ComponentJsonFieldSerializer can only be used with Json format")

        val jsonObject = buildJsonObject {
            put("type", JsonPrimitive(value.type))
            put("data", value.data)
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }
}
