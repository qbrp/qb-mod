package org.qbrp.main.core.events.component

import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.events.EventHandler
import org.qbrp.main.core.events.EventListener
import org.qbrp.main.core.events.EventType

typealias ComponentFilter = (AnyComponent) -> Boolean
typealias ComponentHandler<R> = (R, AnyComponent) -> Unit

class ComponentEventListener<R: Any>(event: EventType<R>, priority: Int, handler: PropagateHandler<R>): EventListener<R, Unit>(event, priority, handler)