package org.qbrp.main.core.networking.serialization.mapping

import org.qbrp.main.core.components.serialization.DTO
import kotlin.reflect.KClass

interface DtoMapperRegistry<O: DTO, D: Any> {
    fun registerFromDtoStrategy(strategy: FromDtoStrategy<out O, out D>, dtoClass: KClass<out O>, objClass: KClass<out D>)
    fun registerToDtoStrategy(strategy: ToDtoStrategy<out O, out D>, dtoClass: KClass<out O>, objClass: KClass<out D>)
    fun register(mapper: DtoMapper<out O, out D>, dtoClass: KClass<out O>, objClass: KClass<out D>) {
        registerToDtoStrategy(mapper, dtoClass, objClass)
        registerFromDtoStrategy(mapper, dtoClass, objClass)
    }
}
