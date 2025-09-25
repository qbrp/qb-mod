package org.qbrp.main.core.components

import org.qbrp.main.core.components.serialization.DTO

interface DtoProvider {
    val dto: DTO? get() = null
}