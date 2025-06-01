package org.qbrp.main.core.game.model.storage

import net.minecraft.world.World
import org.qbrp.main.core.game.model.objects.BaseObject

interface Storage<K, T : BaseObject> {
    fun add(obj: T)
    fun remove(key: K): Boolean
    fun getByKey(key: K): T?
    fun getAll(): Collection<T>
}
