package org.qbrp.main.core.events.component

import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.events.EventHandler

class PropagateHandler<R: Any>(private val storage: EventSupportersStorage, private val filter: ComponentFilter, private val handler: ComponentHandler<R>): EventHandler<R, Unit> {
    override suspend fun invoke(p1: R, p2: R) {
        storage.forEach { it ->
            it.getComponents()
                .filter(filter)
                .forEach({ component -> handler(p1, component) })
        }
    }
}