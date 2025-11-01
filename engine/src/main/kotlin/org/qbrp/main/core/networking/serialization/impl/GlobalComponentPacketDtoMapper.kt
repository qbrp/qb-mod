package org.qbrp.main.core.networking.serialization.impl

import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.mapping.FromDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.ToDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.core.networking.serialization.mapping.ctx.ComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualFromDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualToDtoStrategy
import kotlin.reflect.KClass

class GlobalComponentPacketDtoMapper: ComponentPacketDtoMapper<DTO, Any, ComponentManager>, ComponentPacketDtoMapperRegistry, SerializerKeyRegistry {
    private val toDtoMap = mutableMapOf<KClass<*>, ContextualToDtoStrategy<*, *, ComponentDtoMappingContext<*>>>()
    private val fromDtoMap = mutableMapOf<KClass<out DTO>, ContextualFromDtoStrategy<*, *,  ComponentDtoMappingContext<*>>>()

    @Suppress("UNCHECKED_CAST")
    override fun <O : DTO, D : Any> registerFromDtoStrategy(
        strategy: ContextualFromDtoStrategy<O, D, *>,
        objClass: KClass<D>,
        dtoClass: KClass<O>
    ) {
        fromDtoMap[dtoClass as KClass<out DTO>] = strategy as ContextualFromDtoStrategy<*, *, ComponentDtoMappingContext<*>>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <O : DTO, D : Any> registerToDtoStrategy(
        strategy: ContextualToDtoStrategy<O, D, *>,
        objClass: KClass<D>,
        dtoClass: KClass<O>
    ) {
        toDtoMap[objClass] = strategy as ContextualToDtoStrategy<*, *, ComponentDtoMappingContext<*>>
    }

    override fun fromDto(
        dto: DTO,
        ctx: ComponentDtoMappingContext<ComponentManager>
    ): Any {
        val mapper = fromDtoMap[dto::class]
            ?: error("No mapper registered for DTO: ${dto::class.simpleName}")
        return (mapper as ContextualFromDtoStrategy<DTO, Any, ComponentDtoMappingContext<*>>).fromDto(dto, ctx)
    }

    override fun toDto(
        obj: Any,
        ctx: ComponentDtoMappingContext<ComponentManager>
    ): DTO {
        val mapper = toDtoMap[obj::class]
            ?: error("No mapper registered for object: ${obj::class.simpleName}")
        return (mapper as ContextualToDtoStrategy<DTO, Any, ComponentDtoMappingContext<*>>).toDto(obj, ctx)
    }

    override fun getSerializerKeyNull(obj: Any): SerializerRegistryId? {
        val mapper = toDtoMap[obj::class]
        return (mapper as? DtoSerializable)?.serializerRegistryId
    }
}