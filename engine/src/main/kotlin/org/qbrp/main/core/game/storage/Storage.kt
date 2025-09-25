package org.qbrp.main.core.game.storage

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.StateEntry
import org.qbrp.main.core.components.serialization.Identifiable
import java.util.Spliterator

interface Storage<T : Identifiable>: KoinComponent, ObjectProvider<T>, Iterable<T> {
    fun add(obj: T): T
    fun remove(key: String): Boolean
    fun getAll(): Collection<T>
    fun enableComponent(id: String) {
        filterIsInstance<StateEntry>()
            .forEach {
                it.state.namedIterator().forEach { (n, c) ->
                    if (n.type == id && c is Activateable) c.enable()
                }
            }
    }
    fun disableComponent(id: String) {
        filterIsInstance<StateEntry>()
            .forEach {
                it.state.namedIterator().forEach { (n, c) ->
                    if (n.type == id && c is Activateable) c.disable()
                }
            }
    }
    fun putIfAbsent(obj: T): T? {
        val existing = getAll().find { it.id == obj.id }
        return if (existing == null) {
            add(obj)
            null
        } else {
            existing
        }
    }
    fun clear()
    override fun iterator(): Iterator<T> = getAll().iterator()
    override fun spliterator(): Spliterator<T> = getAll().spliterator()
}
