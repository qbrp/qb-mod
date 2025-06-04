package org.qbrp.main.core.assets.prefabs

interface PrefabsAPI {
    fun <T : Prefab> getByKey(key: PrefabKey): T?
    fun <T : Prefab> loadByKey(key: PrefabKey): T?
    fun registerPrefabCategory(name: String)
}