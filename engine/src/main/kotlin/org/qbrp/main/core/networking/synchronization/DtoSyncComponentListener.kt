package org.qbrp.main.core.networking.synchronization

import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.serialization.DTO

interface DtoSyncComponentListener<D: DTO, C: AnyComponent> {
    fun componentExists(dto: D, component: C)
}