package org.qbrp.main.core.events

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.events.component.ComponentEventContext
import org.qbrp.main.core.events.component.ComponentEventListener
import org.qbrp.main.core.events.component.ComponentHandler
import org.qbrp.main.core.events.component.PropagateHandler

typealias TypedComponentHandler<E, C> = (E, C) -> Unit

class DefaultEventContext(
    override val eventBus: EventBus,
    override val eventSupportersStorage: EventSupportersStorage
) : ComponentEventContext, EventContext

interface EventEnv

fun <R> KoinComponent.withEventContext(block: context(ComponentEventContext) () -> R): R {
    with(get<DefaultEventContext>()) {
        return block()
    }
}

inline fun <reified E: Event> EventBus.on(id: EventId = EventId.ofKclass(E::class),
                                          priority: Int = EventPriority.BASE,
                                          noinline handler: EventHandler<E, Unit>): EventListener<E, Unit> {
    val listener = EventListener(EventType(id), priority, handler)
    subscribe(listener)
    return listener
}

context(ctx: EventContext)
inline fun <reified E: Event, R> EventEnv.on(id: EventId,
                                             priority: Int = EventPriority.BASE,
                                             noinline handler: EventHandler<E, R>) {
    ctx.eventBus.subscribe(EventListener<E, R>(EventType(id), priority, handler))
}

@JvmName("onGenericEvent")
context(ctx: EventContext)
inline fun <reified E: Event, R> EventEnv.on(priority: Int = EventPriority.BASE,
                                          noinline handler: EventHandler<E, R>) {
    on<E, R>(EventId.ofKclass(E::class), priority, handler)
}


@JvmName("onKlassEvent")
context(ctx: EventContext)
inline fun <reified E: Event> EventEnv.on(priority: Int = EventPriority.BASE,
                                          noinline handler: EventHandler<E, Unit>) {
    on<E, Unit>(EventId.ofKclass(E::class), priority, handler)
}

@JvmName("onGenericEventWithId")
context(ctx: EventContext)
inline fun <reified E: Event> EventEnv.on(id: EventId,
                                          priority: Int = EventPriority.BASE,
                                          noinline handler: EventHandler<E, Unit>) {
    ctx.eventBus.subscribe(EventListener(EventType(id), priority, handler))
}

context(ctx: EventContext)
inline fun <reified E: Event> EventEnv.onComponentEvent(id: EventId,
                                                      priority: Int = EventPriority.BASE,
                                                      handler: PropagateHandler<E>) {
    ctx.eventBus.subscribe(ComponentEventListener(EventType(id), priority, handler))
}


@JvmName("onComponentEventByKclass")
context(ctx: EventContext)
inline fun <reified E: Event> EventEnv.onComponentEvent(priority: Int = EventPriority.BASE,
                                                        handler: PropagateHandler<E>) {
    onComponentEvent(EventId.ofKclass(E::class), priority, handler)
}


@JvmName("onGenericComponentEvent")
context(ctx: ComponentEventContext)
inline fun <reified E: Event> EventEnv.onComponentEvent(priority: Int = EventPriority.BASE,
                                                        noinline handler: ComponentHandler<E>) {
    onComponentEvent(
        EventId.ofKclass(E::class),
        priority,
        PropagateHandler(ctx.eventSupportersStorage, { true }, handler)
    )
}

@JvmName("onComponentEventTyped")
context(ctx: ComponentEventContext)
inline fun <reified E: Event, reified R : AnyComponent> EventEnv.onComponentEvent(
    priority: Int = EventPriority.BASE,
    noinline handler: TypedComponentHandler<E, R>
) {
    onComponentEvent(
        EventId.ofKclass(E::class),
        priority,
        PropagateHandler(ctx.eventSupportersStorage, { it is R }, handler as ComponentHandler<E>)
    )
}
context(ctx: EventContext)
inline fun <reified R : Any> Event.collect(withId: EventId = EventId.ofKclass(this::class)): Collection<R> {
    return collect(withId, R::class)
}

context(ctx: EventContext)
inline fun <reified E : Event> E.emit(withId: EventId = EventId.ofKclass(E::class), post: E.(E) -> Unit = {}) {
    collect<Unit>(withId)
    post(this)
}

context(ctx: EventContext)
inline fun <reified R> Event.collectLists(withId: EventId = EventId.ofKclass(this::class)) =
    collect<Collection<R>>(withId).flatMap { it }

fun Iterable<Boolean>.allTrue(): Boolean = all { it }
fun Iterable<Boolean>.allFalse(): Boolean = all { !it }