package org.qbrp.main.core.events

import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class SynchronousEventBus() : EventBus {
    private val listeners: ConcurrentHashMap<AnyEventType, CopyOnWriteArrayList<AnyListener>> = ConcurrentHashMap()

    override fun subscribe(listener: AnyListener) {
        val ls = listeners.getOrPut(listener.event) { CopyOnWriteArrayList() }.apply { add(listener) }
        listeners[listener.event] = CopyOnWriteArrayList<AnyListener>(ls.sortedByDescending { it.priority })
    }

    override fun unsubscribe(listener: AnyListener) {
        listeners[listener.event]!!.remove(listener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E : Event, R : Any> emit(type: EventType<E, R>, event: E): Collection<R> = runBlocking {
        val snapshot = listeners[type].orEmpty()

        val out = mutableListOf<R>()

        for (l in snapshot) {
            if ((event as? Cancellable)?.cancelled == true) break
            val h = l as? EventListener<E, R> ?: continue
            try {
                h.invoke(event).let { if (type.resultClass.isInstance(it)) out.add(it) }
            } catch (t: Throwable) {
                throw t
            }
        }
        return@runBlocking out
    }
}