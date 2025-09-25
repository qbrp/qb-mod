package org.qbrp.main.core.events

open class EventListener<E: Any, R>(val event: EventType<E>, val priority: Int, val handler: EventHandler<E, R>) {
    open suspend fun invoke(event: E): R {
        return handler.invoke(event, event)
    }
}