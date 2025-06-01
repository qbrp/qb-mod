package org.qbrp.main.core.assets.prefabs

import org.qbrp.main.core.Core
import org.qbrp.main.core.assets.common.references.FileReference
import org.qbrp.main.core.assets.common.references.JsonFileReference

class PrefabImporter {
    fun loadPrefab(key: PrefabKey) : Prefab {
        val file = JsonFileReference<Prefab>(key, Prefab::class)
        return Core.ASSETS.load(file)
    }

    fun loadPrefab(fileReference: FileReference<Prefab>) : Prefab {
        return Core.ASSETS.load(fileReference)
    }

}