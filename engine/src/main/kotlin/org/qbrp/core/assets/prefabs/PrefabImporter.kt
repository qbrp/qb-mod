package org.qbrp.core.assets.prefabs

import org.qbrp.core.assets.Assets
import org.qbrp.core.assets.common.files.FileReference
import org.qbrp.core.assets.common.files.JsonFileReference

class PrefabImporter {
    fun loadPrefab(key: PrefabKey) : Prefab {
        val file = JsonFileReference<Prefab>(key, Prefab::class.java)
        return Assets.load(file)
    }

    fun loadPrefab(fileReference: FileReference<Prefab>) : Prefab {
        return Assets.load(fileReference)
    }

}