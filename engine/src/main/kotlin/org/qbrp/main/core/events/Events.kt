package org.qbrp.main.core.events

import kotlin.reflect.KClass

fun <E : Event, R : Any> collect(type: EventType<E, R>, event: E, bus: EventBus): Collection<R> {
    return bus.emit(type, event)
}

context(ctx: EventContext)
fun <E : Event, R : Any> collect(type: EventType<E, R>, event: E): Collection<R> {
    return ctx.eventBus.emit(type, event)
}

inline fun <reified R : Any> Event.collect(
    context: EventContext = GlobalEventContext
): Collection<R> {
    return collect(
        EventType<Event, R>(
            EventId.ofKclass(this::class),
            this::class as KClass<Event>,
            R::class
        ),
        this, context.eventBus
    )
}

//@JvmName("collectExtension3")
//inline fun <reified E : Event, reified R : Any> E.collect(
//    context: EventContext = GlobalEventContext.Sync
//): Collection<R> {
//    return collect(EventType<E, R>(), context)
//}

@JvmName("collectExtension2")
inline fun <reified E : Event, reified R : Any> EventType<E, R>.collect(
    event: E,
    context: EventContext = GlobalEventContext.Sync
): Collection<R> {
    return collect(this, event, context.eventBus)
}

@JvmName("collectExtension1")
inline fun <reified E : Event> EventType<E, Unit>.fire(
    event: E,
    context: EventContext = GlobalEventContext.Sync
) {
    collect(event, context)
}

inline fun <reified E : Event> E.fire(
    type: EventType<E, Unit> = EventType<E>(),
    context: EventContext = GlobalEventContext.Sync
) {
    type.fire(this, context)
}

inline fun <reified E : Event> E.emit(
    type: EventType<E, Unit> = EventType<E>(),
    context: EventContext = GlobalEventContext.Sync,
    crossinline let: (E) -> Unit = {}
) {
    this@emit.fire(type, context)
    let(this@emit)
}

class Subscription<E : Event, R : Any>(val listener: EventListener<E, R>, val inBus: EventBus) {
    fun unsubscribe() {
        inBus.unsubscribe(listener)
    }
}

fun <E : Event, R : Any> on(
    listener: EventListener<E, R>,
    bus: EventBus = GlobalEventContext.eventBus
): Subscription<E, R> {
    bus.subscribe(listener)
    return Subscription(listener, bus)
}

fun <E : Event, R : Any> on(
    type: EventType<E, R>,
    bus: EventBus,
    priority: Int = 5,
    handler: EventHandler<E, R>,
): Subscription<E, R> {
    val listener = type.listener(handler, priority)
    bus.subscribe(listener)
    return Subscription(listener, bus)
}

fun <E : Event, R : Any> on(
    type: EventType<E, R>,
    priority: Int = 5,
    context: EventContext = GlobalEventContext,
    handler: EventHandler<E, R>,
): Subscription<E, R> {
    return on(type, context.eventBus, priority, handler)
}

inline fun <reified E : Event, reified R : Any> on(
    priority: Int = 5,
    context: EventContext = GlobalEventContext,
    noinline handler: EventHandler<E, R>,
): Subscription<E, R> {
    return on(EventType<E, R>(), priority, context, handler)
}

@JvmName("onUnitEvent")
inline fun <reified E : Event> on(
    priority: Int = 5,
    context: EventContext = GlobalEventContext,
    noinline handler: EventHandler<E, Unit>,
): Subscription<E, Unit> {
    return on(EventType<E>(), priority, context, handler)
}

