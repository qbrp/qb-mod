package org.qbrp.main.core.events

import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

interface Event {
    context(ctx: EventContext)
    fun <R : Any> collect(withId: EventId = EventId.ofKclass(this::class), resultKClass: KClass<R>): Collection<R> {
        val eventType = EventType<Event>(withId)
        return runBlocking { ctx.eventBus.emit(eventType, this@Event, resultKClass) }
    }
}