package org.qbrp.main.core.events

interface EventEnv

inline fun <reified R : Any> Event.collectLists() = collect<Collection<R>>().flatMap { it }

inline fun <reified E : Event, reified R : Any> EventType<E, Collection<R>>.collectLists(e: E, context: EventContext = GlobalEventContext) =
    collect<E, Collection<R>>(this, e, context.eventBus).flatMap { it }

fun Iterable<Boolean>.allTrue(): Boolean = all { it }
fun Iterable<Boolean>.allFalse(): Boolean = all { !it }