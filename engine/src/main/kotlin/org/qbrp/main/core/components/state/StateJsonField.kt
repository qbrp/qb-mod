package org.qbrp.main.core.components.state

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import org.qbrp.main.core.components.serialization.ComponentJsonField

@Serializable(with = StateJsonFieldSerializer::class)
data class StateJsonField(val components: List<ComponentJsonField>) : Iterable<ComponentJsonField> {
    override fun iterator(): Iterator<ComponentJsonField> = components.iterator()
}

object StateJsonFieldSerializer : KSerializer<StateJsonField> {
    override val descriptor: SerialDescriptor =
        ListSerializer(ComponentJsonField.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: StateJsonField) {
        val listSerializer = ListSerializer(ComponentJsonField.serializer())
        encoder.encodeSerializableValue(listSerializer, value.components)
    }

    override fun deserialize(decoder: Decoder): StateJsonField {
        val listSerializer = ListSerializer(ComponentJsonField.serializer())
        val list = decoder.decodeSerializableValue(listSerializer)
        return StateJsonField(list)
    }
}