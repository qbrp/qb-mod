package org.qbrp.main.core.events

typealias AnyListener = EventListener<*, *>

typealias EventHandler<E, R> = suspend E.() -> R

open class EventListener<E : Event, R : Any>(
    val event: EventType<E, R>,
    val priority: Int,
    val handler: EventHandler<E, R>
) {
    open suspend fun invoke(event: E): R {
        return handler.invoke(event)
    }
}

fun <E : Event, R : Any> EventType<E, R>.listener(
    handler: EventHandler<E, R>,
    priority: Int = 5
): EventListener<E, R> {
    return EventListener(
        this,
        priority,
        handler
    )
}