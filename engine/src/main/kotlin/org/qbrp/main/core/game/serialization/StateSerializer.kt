package org.qbrp.main.core.game.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.qbrp.main.core.game.model.State
import kotlin.collections.forEach
import kotlin.reflect.full.hasAnnotation

object StateSerializer : KSerializer<State> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("State")

    override fun serialize(encoder: Encoder, value: State) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("StateSerializer can only be used with JSON")

        val state = value.copy()
        val componentsToSave = mutableListOf<ComponentJsonField>()

        state.jsonComponents.forEach {
            if (state.componentsMap[it.getComponentName()] == null) {
                componentsToSave.add(it)
            }
        }
        state.componentsMap.forEach { (key, component) ->
            if (component.save && component::class.hasAnnotation<Serializable>()) {
                val jsonElement = jsonEncoder.json.encodeToJsonElement(component)
                componentsToSave.add(ComponentJsonField(key, jsonElement))
            }
        }

        val resultJson = buildJsonObject {
            put("components", JsonArray(componentsToSave.map {
                jsonEncoder.json.encodeToJsonElement(it)
            }))
        }

        jsonEncoder.encodeJsonElement(resultJson)
    }

    override fun deserialize(decoder: Decoder): State {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("StateSerializer can only be used with JSON")

        val jsonElement = jsonDecoder.decodeJsonElement()
        val jsonObject = jsonElement.jsonObject

        val componentsArray = jsonObject["components"]?.jsonArray
            ?: error("Missing 'components' array in JSON")

        val jsonComponents = componentsArray.map {
            jsonDecoder.json.decodeFromJsonElement(ComponentJsonField.serializer(), it)
        }

        return State(jsonComponents = jsonComponents)
    }
}