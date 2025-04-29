package org.qbrp.core.game.model.components.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.koin.core.context.GlobalContext
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.model.components.Component
import org.qbrp.core.game.prefabs.Prefab.PrefabField

@JsonDeserialize(using = CjfDeserializer::class)
data class ComponentJsonField(val type: String, val data: Component): PrefabField {

    companion object {
        val MAPPER = ObjectMapper().apply { registerKotlinModule() }
    }

    inline fun <reified T> cast(): T {
        val klass = GlobalContext.get().get<ComponentsRegistry>().getComponent(type)
        return MAPPER.convertValue(data, klass) as T
    }

    override fun component(): Component {
        return cast<Component>()
    }

}