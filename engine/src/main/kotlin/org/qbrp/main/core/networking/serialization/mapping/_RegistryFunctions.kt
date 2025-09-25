package org.qbrp.main.core.networking.serialization.mapping

import dev.felnull.specialmodelloader.impl.include.de.javagl.obj.Obj
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.networking.serialization.impl.DataComponentMapper
import org.qbrp.main.core.networking.serialization.mapping.ctx.ContextualDtoMapperRegistry
import org.qbrp.main.core.networking.serialization.serializerId
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.engine.text.map.TextMappingRegistry

inline fun <reified C: DataComponent> DtoMapperRegistry<C, C>.registerDataComponentMapper() {
    register(
        DataComponentMapper<C>(C::class.serializerId()),
        C::class,
        C::class
    )
}

inline fun <reified C: DataComponent> ContextualDtoMapperRegistry.registerDataComponentMapper() {
    register<C, C>(DataComponentMapper<C>(C::class.serializerId()), C::class, C::class)
}

inline fun <D: DTO, C: Any, reified DtoTyped : D> DtoMapper<D, C>.toDtoTyped(obj: C): DtoTyped {
    val dto = toDto(obj)
    return dto as? DtoTyped
        ?: error("Expected ${DtoTyped::class}, but got ${dto::class}")
}

inline fun <D: DTO, C: Any, reified ObjTyped : C> DtoMapper<D, C>.fromDtoTyped(dto: D): ObjTyped {
    val obj = fromDto(dto)
    return obj as? ObjTyped
        ?: error("Expected ${ObjTyped::class}, but got ${obj::class}")
}


inline fun <reified D: TextDto> TextMappingRegistry.toDtoTyped(obj: TextComponent): D {
    return toDtoTyped<TextDto, TextComponent, D>(obj)
}

inline fun <reified C: TextComponent> TextMappingRegistry.fromDtoTyped(dto: TextDto): C {
    return fromDtoTyped<TextDto, TextComponent, C>(dto)
}