package org.qbrp.main.core.game.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.qbrp.main.core.Core
import org.qbrp.main.core.game.ComponentsRegistry
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.game.prefabs.PrefabField
import org.qbrp.main.engine.Engine
import org.qbrp.main.core.game.GameEngine

@Serializable(with = ComponentJsonFieldSerializer::class)
data class ComponentJsonField(
    val type: String,
    val data: JsonElement
) : PrefabField {

    fun getComponentName(): String =
        type.substringAfter("#", type.substringBefore("#"))


    fun toComponent(): Component {
        val registry = Core.getModule<GameEngine>().getLocal<ComponentsRegistry>()
        val serializer = GameMapper.getDeserializer(
            registry.getComponentClass(type)
        )

        return GameMapper.COMPONENTS_JSON.decodeFromJsonElement(
            serializer,
            data
        )
    }

    override fun component(): Component {
        return toComponent()
    }
}
