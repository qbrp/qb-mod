package org.qbrp.core.game.model.components.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.qbrp.core.game.model.components.Component

class StateSerializer : StdSerializer<MutableMap<*, *>>(MutableMap::class.java) {
    override fun serialize(
        value: MutableMap<*, *>,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        @Suppress("UNCHECKED_CAST")
        val typed: MutableMap<String, Component> = (value as? MutableMap<*, *>)?.let {
            if (it.keys.all { k -> k is String } && it.values.all { v -> v is Component }) {
                @Suppress("UNCHECKED_CAST")
                it as MutableMap<String, Component>
            } else null
        } ?: run {
            val std = provider.findValueSerializer(value.javaClass, null)
            std.serialize(value, gen, provider)
            return
        }
        gen.writeStartArray()
        typed.forEach { (key, component) ->
            if (component.save) gen.writeObject(ComponentJsonField(key, component))
        }
        gen.writeEndArray()
    }
}
