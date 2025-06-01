package org.qbrp.main.engine.assets.resourcepack.versioning

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.get
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.FileSystem.getOrCreate
import org.qbrp.main.core.game.serialization.GameMapper
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.assets.patches.Manifest
import org.qbrp.main.engine.assets.patches.PatchesAPI
import org.qbrp.main.engine.assets.resourcepack.baking.ResourcePackAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.InfoNames.CONTENTPACKS_ENABLED
import org.qbrp.main.core.utils.networking.info.ServerInfoAPI
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import java.io.File

@Autoload(LoadPriority.MODULE - 1)
class ResourcePackVersionsModule : QbModule("resourcepack-versions"), ResourcePackVersionsAPI {

    companion object {
        val CONTENTPACKS_PATH = getOrCreate(FileSystem.HTTP_SERVER_ASSETS.resolve("contentpacks"), true)
        val CONTENTPACKS_PATCHES_PATH = getOrCreate(FileSystem.HTTP_SERVER_ASSETS.resolve("contentpacks-patches"), true)
    }

    private lateinit var patchesAPI: PatchesAPI
    private lateinit var repository: VersionRepository
    private var version: String = "3.0.0"

    override fun getKoinModule() = inner<ResourcePackVersionsAPI>(this) {
        scoped { VersionRepository(CONTENTPACKS_PATH, CONTENTPACKS_PATCHES_PATH)  }
    }

    init {
        dependsOn { Engine.isApiAvailable<PatchesAPI>() }
        dependsOn { Engine.isApiAvailable<ResourcePackAPI>() }
    }

    override fun onEnable() {
        get<ServerInfoAPI>().COMPOSER.component(CONTENTPACKS_ENABLED, true)
        patchesAPI = get()
        repository = getLocal()
        createVersion(version)
        generatePatchesToVersion(version)
    }

    private fun createVersion(version: String) {
        val resourcePack = repository.getVersion(version)
        resourcePack.bake()
        resourcePack.zipUp()
    }

    private fun generatePatchesToVersion(version: String) {
        val newVersion = repository.getVersion(version)

        repository.listVersions().forEach { old ->
            if (old != version && old != "temp") {
                val oldVersion = repository.getVersion(old)
                val patch = Patch(oldVersion, newVersion, patchesAPI, CONTENTPACKS_PATCHES_PATH)
                val patchDir = patch.create()
                FileSystem.zipDirectoryTo(
                    patchDir.resolve("pack.zip"),
                    patchDir.resolve("zip").toPath()
                )
            }
        }
    }

    override fun getLatestVersionFile(): File {
        return repository.getVersion(version).baseDir
    }

    override fun getPacksFile(): File = CONTENTPACKS_PATH

    override fun getPatchesFile(): File = CONTENTPACKS_PATCHES_PATH

    override fun getVersion(): String = version

    override fun isPatchExists(oldVersion: String, newVersion: String): Boolean {
        return repository.getPatch(oldVersion, newVersion).exists()
    }

    override fun createResourcePackEntry(version: String): ContentPackVersion {
        val resourcePack = repository.getVersion(version)
        resourcePack.bake()
        return resourcePack
    }

    override fun createPatch(oldVersion: String, newVersion: String): Patch {
        val oldVer = repository.getVersion(oldVersion)
        val newVer = repository.getVersion(newVersion)
        return Patch(oldVer, newVer, patchesAPI, CONTENTPACKS_PATCHES_PATH).also { it.create() }
    }
}