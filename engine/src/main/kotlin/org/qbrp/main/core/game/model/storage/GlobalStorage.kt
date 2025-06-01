package org.qbrp.main.core.game.model.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.minecraft.world.World
import org.qbrp.main.core.game.model.objects.BaseObject

open class GlobalStorage<K, T: BaseObject>: Storage<K, T> {
    protected val objects: MutableList<T> = ArrayList()
    protected val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun checkIdCollision(id: Long): Boolean {
        objects.count { it.id == id }.let {
            return it > 1
        }
    }

    override fun add(obj: T) {
        objects.add(obj)
        scope.launch {
            if (checkIdCollision(obj.id))
                throw RuntimeException("Обнаружено пересечение ID ${obj.id} двух объектов")
        }
    }

    override fun remove(id: K): Boolean {
        return objects.removeIf { it.id == id }
    }

    override fun getByKey(id: K): T? {
        return objects.find { it.id == id }
    }

    override fun getAll(): Collection<T> {
        return objects
    }
}