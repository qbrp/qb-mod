package org.qbrp.core.game.model.components.json

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.koin.core.context.GlobalContext
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.model.components.Component
import org.qbrp.core.game.prefabs.PrefabField

@JsonDeserialize(using = CjfDeserializer::class)
data class ComponentJsonField(
    val type: String,
    val data: JsonNode
) : PrefabField {

    companion object {
        // Один маппер на всё приложение
        val MAPPER = ObjectMapper().apply { registerKotlinModule() }
    }

    @JsonIgnore
    fun getComponentName(): String =
        type.substringAfter("#", type.substringBefore("#"))


    fun toComponent(registry: ComponentsRegistry = GlobalContext.get().get<ComponentsRegistry>()): Component {
        val klass = registry.getComponent(type)
        return MAPPER.treeToValue(data, klass) as Component
    }

    override fun component(): Component {
        return toComponent()
    }
}
