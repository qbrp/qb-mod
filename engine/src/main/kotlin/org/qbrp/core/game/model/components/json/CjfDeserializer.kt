package org.qbrp.core.game.model.components.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.koin.core.context.GlobalContext
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.model.components.Component

@JsonDeserialize(using = CjfDeserializer::class)
class CjfDeserializer
    : StdDeserializer<ComponentJsonField>(ComponentJsonField::class.java) {

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): ComponentJsonField {
        val node = p.codec.readTree<JsonNode>(p)
        val type = node.get("type").asText()
        val dataNode = node.get("data")
        return ComponentJsonField(type, dataNode)
    }

}