package org.qbrp.main.core.networking.serialization.mapping.ctx

import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.networking.serialization.mapping.DtoMapper
import org.qbrp.main.core.networking.serialization.mapping.FromDtoStrategy
import org.qbrp.main.core.networking.serialization.mapping.ToDtoStrategy
import kotlin.reflect.KClass

interface ContextualDtoMapperRegistry {
    fun <O : DTO, D : Any> registerFromDtoStrategy(strategy: ContextualFromDtoStrategy<O, D, *>, objClass: KClass<D>, dtoClass: KClass<O>)
    fun <O : DTO, D : Any> registerToDtoStrategy(strategy: ContextualToDtoStrategy<O, D, *>, objClass: KClass<D>, dtoClass: KClass<O>)
    fun <O : DTO, D : Any> register(mapper: ContextualDtoMapper<O, D, *>, objClass: KClass<D>, dtoClass: KClass<O>) {
        registerToDtoStrategy(mapper, objClass, dtoClass)
        registerFromDtoStrategy(mapper, objClass, dtoClass)
    }
}
