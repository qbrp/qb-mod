package org.qbrp.main.core.game.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.qbrp.main.core.components.serialization.Identifiable

open class GlobalStorage<T: Identifiable>: Storage<T>{
    protected val objects: MutableList<T> = ArrayList()
    protected val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun checkIdCollision(id: String): Boolean {
        objects.toList().count { it.id == id }.let {
            return it > 1
        }
    }
    override fun add(obj: T): T {
        objects.add(obj)
        if (checkIdCollision(obj.id))
            throw RuntimeException("Обнаружено пересечение ID ${obj.id} двух объектов (${this@GlobalStorage::class.qualifiedName})")
//        scope.launch {
//            if (checkIdCollision(obj.id))
//                throw RuntimeException("Обнаружено пересечение ID ${obj.id} двух объектов (${this@GlobalStorage::class.qualifiedName})")
//        }
        return obj
    }

    override fun remove(id: String): Boolean {
        return objects.removeIf { it.id == id }
    }

    override fun getById(id: String): T? {
        return objects.find { it.id == id }
    }

    override fun getAll(): Collection<T> {
        return objects
    }

    override fun clear() = objects.clear()
}