package org.qbrp.main.core.events

import kotlin.reflect.KClass

typealias AnyEventType = EventType<*, *>

data class EventType<E : Event, R : Any>(val id: EventId, val eventClass: KClass<E>, val resultClass: KClass<R>)

inline fun <reified E : Event, reified R : Any> EventType(
    id: String
) = EventType(EventId(id), E::class, R::class)

inline fun <reified E : Event, reified R : Any> EventType(
    id: EventId = EventId.ofKclass(E::class)
) = EventType(id, E::class, R::class)

@JvmName("unitEventType")
inline fun <reified E : Event> EventType(
    id: EventId = EventId.ofKclass(E::class)
) = EventType(id, E::class, Unit::class)