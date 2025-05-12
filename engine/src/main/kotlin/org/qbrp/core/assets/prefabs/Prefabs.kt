package org.qbrp.core.assets.prefabs

import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.Assets
import org.qbrp.core.assets.common.files.JsonFileReference

object Prefabs: PrefabsAPI {
    val importer = PrefabImporter()

    override fun <T : Prefab> getByKey(key: PrefabKey): T? {
        return (Assets.getByKey<Prefab>(key) ?: importer.loadPrefab(key)) as T?
    }

    fun registerPrefabCategory(name: String) {
        FileSystem.PREFABS.resolve(name).mkdirs()
    }

    fun initializePrefabs() {
        val prefabsDir = FileSystem.PREFABS
        val categories = prefabsDir.listFiles()
        for (category in categories) {
            category.walkTopDown().forEach {
                if (it.name.endsWith(".json")) {
                    val reference = JsonFileReference<Prefab>(PrefabKey(category.name, it.nameWithoutExtension), Prefab::class.java)
                    importer.loadPrefab(reference)
                }
            }
        }
    }
}