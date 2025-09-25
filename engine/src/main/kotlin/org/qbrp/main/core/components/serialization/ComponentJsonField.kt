package org.qbrp.main.core.components.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.Name
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry

@Serializable(with = ComponentJsonFieldSerializer::class)
data class ComponentJsonField(
    val type: String,
    val data: JsonElement
) : KoinComponent {

    fun getComponentName(): String =
        type.substringAfter("#", type.substringBefore("#"))

    fun getComponentType(): String =
        type.substringBefore("#", type.substringAfter("#"))

    fun toComponent(operator: ComponentManager,
                    serializers: SerializersRegistry = GlobalContext.get().get(),
                    mapper: ComponentDtoMapper = GlobalContext.get().get(),
                    registry: ComponentRegistry = GlobalContext.get().get()
    ): AnyComponent {
        val entry = registry.getEntry(getComponentType())
        val ctx = ComponentMapContext(operator, getComponentType())
        return try {
            val deserializer = entry.deserializer
                ?: serializers.getDeserializationStrategy(SerializerRegistryId.ofComponent(getComponentType()))
            val data = Serialization.JSON.decodeFromJsonElement(
                deserializer,
                data
            )
            val dto = mapper.fromDto(data, ctx)
            dto
        } catch (e: Exception) {
            entry.constructor?.let { it(operator) } ?: throw e
        }
    }

    companion object {
        fun from(component: AnyComponent,
                 name: Name,
                 context: ComponentMapContext,
                 serializers: SerializersRegistry,
                 mapper: ComponentDtoMapper = GlobalContext.get().get()): ComponentJsonField {
            val entry = mapper.toDto(component, context)
            val serializer = serializers.getSerializationStrategy<DTO>(mapper.getSerializerKey(component))
            val json = Json.encodeToJsonElement(serializer, entry)
            return ComponentJsonField(name.full, json)
        }
    }
}
