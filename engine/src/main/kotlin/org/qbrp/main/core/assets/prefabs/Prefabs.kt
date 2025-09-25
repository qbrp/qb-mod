package org.qbrp.main.core.assets.prefabs

import org.koin.core.component.get
import org.qbrp.main.core.assets.AssetsAPI
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.references.JsonFileReference
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(Priorities.HIGHEST - 1)
class Prefabs: EngineModule("prefabs"), PrefabsAPI {
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

    override fun parseCategory(name: String): List<Prefab> {
        val base = FileSystem.PREFABS
        return base.resolve(name).walkTopDown()
            .filter { it.extension == "json" }
            .map { file ->
                val relative = file.relativeTo(base).invariantSeparatorsPath.removeSuffix(".json")
                importer.loadPrefab(JsonFileReference(PrefabKey(relative), Prefab::class))
            }
            .toList()
    }

    fun initializePrefabs() {
        val base = FileSystem.PREFABS
        base.listFiles()?.forEach { category ->
            category.walkTopDown()
                .filter { it.extension == "json" }
                .forEach { file ->
                    val relative = file.relativeTo(base).invariantSeparatorsPath.removeSuffix(".json")
                    val reference = JsonFileReference(PrefabKey(relative), Prefab::class)
                    importer.loadPrefab(reference)
                }
        }
    }

    private fun validatePath(path: String): String {
        return path.removeSuffix(".json")
    }
}