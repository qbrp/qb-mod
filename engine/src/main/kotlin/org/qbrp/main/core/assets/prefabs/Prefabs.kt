package org.qbrp.main.core.assets.prefabs

import org.koin.core.Koin
import org.koin.core.component.get
import org.koin.core.module.Module
import org.qbrp.main.core.Core
import org.qbrp.main.core.assets.AssetsAPI
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.references.JsonFileReference
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule

@Autoload(LoadPriority.HIGHEST - 1)
class Prefabs: QbModule("prefabs"), PrefabsAPI {
    val importer = PrefabImporter()

    override fun onLoad() {
        initializePrefabs()
    }

    override fun getKoinModule() = onlyApi<PrefabsAPI>(this)

    override fun <T : Prefab> getByKey(key: PrefabKey): T? {
        return (get<AssetsAPI>().getByKey<Prefab>(key) ?: importer.loadPrefab(key)) as T?
    }

    override fun <T : Prefab> loadByKey(key: PrefabKey): T? {
        return importer.loadPrefab(key) as T?
    }

    override fun registerPrefabCategory(name: String) {
        FileSystem.PREFABS.resolve(name).mkdirs()
    }

    fun initializePrefabs() {
        val prefabsDir = FileSystem.PREFABS
        val categories = prefabsDir.listFiles()
        for (category in categories) {
            category.walkTopDown().forEach {
                if (it.name.endsWith(".json")) {
                    val reference = JsonFileReference<Prefab>(PrefabKey(category.name, it.nameWithoutExtension), Prefab::class)
                    importer.loadPrefab(reference)
                }
            }
        }
    }
}