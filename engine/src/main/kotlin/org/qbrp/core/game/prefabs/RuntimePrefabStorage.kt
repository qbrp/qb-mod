package org.qbrp.core.game.prefabs

import net.minecraft.world.World
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.model.storage.Storage

class RuntimePrefabStorage {
    val prefabs = mutableMapOf<String, MutableList<Prefab>>()

    fun addPrefab(prefab: Prefab) {
        prefabs.getOrPut(prefab.category) { mutableListOf() }.add(prefab)
    }

    fun getPrefabTag(category: String, prefab: String, tag: String? = null): Prefab.Tag? {
        val pf = prefabs[category]?.find { it.id == prefab }
        if (tag != null) {
            return pf!!.getTag(tag)
        } else {
            return pf!!.getDefaultTag()
        }
    }
}