package org.qbrp.main.core.components.serialization.map

import kotlinx.serialization.DeserializationStrategy
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.Serialization
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.impl.ComponentDtoMappingContext
import org.qbrp.main.core.networking.serialization.mapping.DtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.FromDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.core.networking.serialization.mapping.ToDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapper
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualFromDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualToDtoStrategy
import kotlin.collections.get
import kotlin.reflect.KClass

class ComponentDtoMapper(): ContextualDtoMapper<DTO, AnyComponent, ComponentMapContext>, ContextualDtoMapperRegistry, SerializerKeyRegistry {
    private val toDtoMap = mutableMapOf<KClass<out Any>, ContextualToDtoStrategy<out DTO, out AnyComponent, ComponentMapContext>>()
    private val fromDtoMap = mutableMapOf<KClass<out DTO>, ContextualFromDtoStrategy<out DTO, out AnyComponent, ComponentMapContext>>()
    private val componentKclassSerializerKeys = mutableMapOf<KClass<out AnyComponent>, SerializerRegistryId>()

    fun registerSerializerKey(forKclass: KClass<out AnyComponent>, id: SerializerRegistryId) {
        componentKclassSerializerKeys[forKclass] = id
    }

    fun <D : Any> hasToDtoStrategy(clazz: KClass<D>): Boolean {
        return toDtoMap.contains(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <O : DTO, D : Any> registerFromDtoStrategy(
        strategy: ContextualFromDtoStrategy<O, D, *>,
        objClass: KClass<D>,
        dtoClass: KClass<O>
    ) {
        fromDtoMap[dtoClass as KClass<out DTO>] = strategy as ContextualFromDtoStrategy<out DTO, out AnyComponent, ComponentMapContext>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <O : DTO, D : Any> registerToDtoStrategy(
        strategy: ContextualToDtoStrategy<O, D, *>,
        objClass: KClass<D>,
        dtoClass: KClass<O>
    ) {
        toDtoMap[objClass] = strategy as ContextualToDtoStrategy<out DTO, out AnyComponent, ComponentMapContext>
    }

    override fun fromDto(
        dto: DTO,
        ctx: ComponentMapContext
    ): AnyComponent {
        return (fromDtoMap[dto::class] as? ContextualFromDtoStrategy<DTO, AnyComponent, ComponentMapContext>)
            ?.fromDto(dto, ctx)
            ?: error("No mapper registered for dto: ${dto::class.simpleName}")
    }

    override fun toDto(
        obj: AnyComponent,
        ctx: ComponentMapContext
    ): DTO =
        (toDtoMap[obj::class] as? ContextualToDtoStrategy<DTO, AnyComponent, ComponentMapContext>)?.toDto(obj, ctx)
        ?: obj.dto
        ?: error("No mapper registered for object: ${obj::class.simpleName}")

    override fun getSerializerKeyNull(obj: Any): SerializerRegistryId? {
        val objClass = obj::class
        val mapper = toDtoMap[objClass]
        return (mapper as? DtoSerializable)?.serializerRegistryId ?: componentKclassSerializerKeys[objClass]
    }
}