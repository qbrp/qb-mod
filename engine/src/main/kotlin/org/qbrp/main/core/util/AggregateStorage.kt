package org.qbrp.main.core.util

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.storage.ObjectProvider
import org.qbrp.main.core.util.storage.Storage

open class AggregateStorage<O: Identifiable>: ObjectProvider<O>, Iterable<O> {
    private val storages: MutableList<Storage<O>> = mutableListOf()
    private val mixedStorage: List<O> get() = storages.flatMap { it.getAll() }

    override fun getById(id: String): O? {
        return mixedStorage.find { it.id == id }
    }

    fun addStorage(storage: Storage<O>) {
        storages += storage
    }

    fun getAll() = mixedStorage.toList()

    override fun iterator(): Iterator<O> {
        return mixedStorage.iterator()
    }
}