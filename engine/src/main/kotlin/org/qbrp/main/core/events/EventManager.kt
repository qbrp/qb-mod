package org.qbrp.main.core.events

import com.mojang.authlib.minecraft.client.MinecraftClient
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import net.minecraft.client.util.NetworkUtils
import net.minecraft.network.NetworkThreadUtils
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.utils.log.LoggerUtil
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

typealias EventHandler<E, R> = suspend E.(E) -> R
typealias AnyEventType = EventType<*>
typealias AnyListener = EventListener<*, *>

class EventManager(): EventBus {
    private val listeners: ConcurrentHashMap<AnyEventType, CopyOnWriteArrayList<AnyListener>> = ConcurrentHashMap()
    private val logger = LoggerUtil.get("events")

    override fun subscribe(listener: AnyListener) {
        listeners.getOrPut(listener.event) { CopyOnWriteArrayList() }.add(listener)
    }

    override fun unsubscribe(listener: AnyListener) {
        listeners[listener.event]!!.remove(listener)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <E : Any, R : Any> emit(type: EventType<E>, event: E, resultClass: KClass<R>): Collection<R> {
        val snapshot = listeners[type].orEmpty().toList()
            .sortedByDescending { it.priority }

        val out = mutableListOf<R>()
        for (l in snapshot) {
            if ((event as? Cancellable)?.cancelled == true) break
            val h = l as? EventListener<E, R> ?: continue
            try {
                h.invoke(event).let { if (resultClass.isInstance(it)) out.add(it) }
            } catch (t: Throwable) {
                throw t
            }
        }
        return out
    }
}