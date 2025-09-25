package org.qbrp.main.core.networking.serialization.impl

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.mapping.DtoMapper
import org.qbrp.main.core.networking.serialization.mapping.DtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.core.networking.serialization.mapping.FromDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.core.networking.serialization.mapping.ToDtoStrategy
import kotlin.reflect.KClass

open class DefaultMapperRegistry<D: DTO, T: Any>: DtoMapper<D, T>, DtoMapperRegistry<D, T>, SerializerKeyRegistry {
    private val toDtoMap = mutableMapOf<KClass<out T>, ToDtoStrategy<out D, out T>>()
    private val fromDtoMap = mutableMapOf<KClass<out D>, FromDtoStrategy<out D, out T>>()

    override fun getSerializerKeyNull(obj: Any): SerializerRegistryId? {
        val mapper = toDtoMap[obj::class]
        return (mapper as? DtoSerializable)?.serializerRegistryId
    }

    override fun fromDto(dto: D): T {
        val mapper = fromDtoMap[dto::class]
            ?: error("No mapper registered for DTO: ${dto::class.simpleName}")
        return (mapper as FromDtoStrategy<DTO, Any>).fromDto(dto as DTO) as T
    }

    override fun toDto(obj: T): D {
        val mapper = toDtoMap[obj::class]
            ?: error("No mapper registered for object: ${obj::class.simpleName}")
        return (mapper as ToDtoStrategy<DTO, Any>).toDto(obj) as D
    }

    override fun registerFromDtoStrategy(
        strategy: FromDtoStrategy<out D, out T>,
        dtoClass: KClass<out D>,
        objClass: KClass<out T>
    ) {
        fromDtoMap[dtoClass as KClass<out D>] = strategy
    }

    override fun registerToDtoStrategy(
        strategy: ToDtoStrategy<out D, out T>,
        dtoClass: KClass<out D>,
        objClass: KClass<out T>
    ) {
        toDtoMap[objClass as KClass<out T>] = strategy
    }
}