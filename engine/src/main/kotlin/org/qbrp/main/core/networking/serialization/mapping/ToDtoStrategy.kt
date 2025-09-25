package org.qbrp.main.core.networking.serialization.mapping

import org.qbrp.main.core.components.serialization.DTO

fun interface ToDtoStrategy<Dto: DTO, Obj: Any> {
    fun toDto(obj: Obj): Dto
}