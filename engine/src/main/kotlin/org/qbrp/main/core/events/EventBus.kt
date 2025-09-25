package org.qbrp.main.core.events

import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

interface EventBus {
    fun subscribe(listener: AnyListener)
    fun unsubscribe(listener: AnyListener)
    suspend fun <E: Any, R: Any> emit(type: EventType<E>, event: E, resultClass: KClass<R>): Collection<R>
}