package org.qbrp.main.core.networking.serialization.mapping

import org.qbrp.main.core.components.serialization.DTO

fun interface FromDtoStrategy<Dto: DTO, Obj: Any> {
    fun fromDto(dto: Dto): Obj
}