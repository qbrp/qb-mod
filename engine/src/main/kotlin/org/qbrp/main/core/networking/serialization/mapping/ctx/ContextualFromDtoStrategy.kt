package org.qbrp.main.core.networking.serialization.mapping.ctx

import org.qbrp.main.core.components.serialization.DTO

fun interface ContextualFromDtoStrategy<Dto: DTO, Obj: Any, Ctx: Any> {
    fun fromDto(dto: Dto, ctx: Ctx): Obj
}