package org.qbrp.main.core.events

import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

interface EventBus {
    fun subscribe(listener: AnyListener)
    fun unsubscribe(listener: AnyListener)
    fun <E : Event, R: Any> emit(type: EventType<E, R>, event: E): Collection<R>
}