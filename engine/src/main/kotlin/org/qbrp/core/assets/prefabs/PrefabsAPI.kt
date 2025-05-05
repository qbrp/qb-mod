package org.qbrp.core.assets.prefabs

interface PrefabsAPI {
    fun <T : Prefab> getByKey(key: PrefabKey): T?
}