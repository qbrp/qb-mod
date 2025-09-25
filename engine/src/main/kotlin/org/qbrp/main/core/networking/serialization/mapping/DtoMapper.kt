package org.qbrp.main.core.networking.serialization.mapping

import org.qbrp.main.core.components.serialization.DTO

interface DtoMapper<Dto: DTO, Obj: Any>: FromDtoStrategy<Dto, Obj>, ToDtoStrategy<Dto, Obj>