package org.qbrp.client.render.ui.view.event

import org.qbrp.client.render.ui.view.ScreenEventHandler
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.EventListener
import org.qbrp.main.core.events.EventType

open class UiEventListener<V : ViewComponent, E : Event, R : Any>(
    eventType: EventType<E, Collection<R>>,
    priority: Int,
    handler: ScreenEventHandler<V, E, R>
) : EventListener<E, Collection<R>>(eventType, priority, handler)