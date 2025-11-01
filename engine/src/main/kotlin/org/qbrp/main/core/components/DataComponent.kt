package org.qbrp.main.core.components

import org.qbrp.main.core.components.serialization.DTO

interface DataComponent : StateUnavailableComponent, DTO {
    override val dto: DTO get() = this
}