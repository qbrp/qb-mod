package org.qbrp.main.core.networking.serialization.mapping.ctx

import org.qbrp.main.core.components.serialization.DTO

interface ContextualDtoMapper<Dto: DTO, Obj: Any, Ctx: Any>: ContextualFromDtoStrategy<Dto, Obj, Ctx>, ContextualToDtoStrategy<Dto, Obj, Ctx>