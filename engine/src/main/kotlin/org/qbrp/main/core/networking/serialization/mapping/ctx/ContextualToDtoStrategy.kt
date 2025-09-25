package org.qbrp.main.core.networking.serialization.mapping.ctx

import org.qbrp.main.core.components.serialization.DTO

interface ContextualToDtoStrategy<Dto: DTO, Obj: Any, Ctx: Any> {
    fun toDto(obj: Obj, ctx: Ctx): Dto
}