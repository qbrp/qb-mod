package org.qbrp.core.game.prefabs

class RuntimePrefabStorage {
    val prefabs = mutableMapOf<String, MutableList<RuntimePrefab>>()

    fun addPrefab(prefab: RuntimePrefab) {
        prefabs.getOrPut(prefab.category) { mutableListOf() }.add(prefab)
    }

    fun getPrefabTag(category: String, prefab: String, tag: String? = null): RuntimePrefab.Tag? {
        val pf = prefabs[category]?.find { it.id == prefab }
        if (tag != null) {
            return pf!!.getTag(tag)
        } else {
            return pf!!.getDefaultTag()
        }
    }
}