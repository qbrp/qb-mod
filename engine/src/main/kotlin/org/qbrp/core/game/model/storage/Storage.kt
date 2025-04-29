package org.qbrp.core.game.model.storage

import net.minecraft.world.World
import org.qbrp.core.game.model.objects.BaseObject

interface Storage<K, T : BaseObject> {
    fun add(obj: T)
    fun remove(key: K): Boolean
    fun getByKey(key: K): T?
    fun getAll(): Collection<T>
    fun tickAll(world: World? = null)
}
