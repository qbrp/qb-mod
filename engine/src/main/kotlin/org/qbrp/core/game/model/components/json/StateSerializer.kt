package org.qbrp.core.game.model.components.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.qbrp.core.game.model.components.Component
import org.qbrp.core.game.model.State
import org.qbrp.core.game.serialization.GameMapper

class StateSerializer : StdSerializer<State>(State::class.java) {
    override fun serialize(
        value: State,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        val state = value.copy()
        gen.writeStartObject()
        gen.writeArrayFieldStart("components")

        val componentsToSave = mutableListOf<ComponentJsonField>()
        state.jsonComponents.forEach {
            if (state.components[it.getComponentName()] == null) componentsToSave.add(it)
        }
        state.components.forEach { (key, value) ->
            if (value.save) componentsToSave.add(ComponentJsonField(key, GameMapper.valueToTree(value)))
        }
        componentsToSave.forEach {
            gen.writeObject(it)
        }

        gen.writeEndArray()
        gen.writeEndObject()
    }

}
