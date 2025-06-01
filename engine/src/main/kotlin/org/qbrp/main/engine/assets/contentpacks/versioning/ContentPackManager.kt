package org.qbrp.main.engine.assets.contentpacks.versioning

import org.koin.core.component.get
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.FileSystem.getOrCreate
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.assets.contentpacks.patches.PatchesAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.InfoNames.CONTENTPACKS_ENABLED
import org.qbrp.main.core.info.ServerInfoAPI
import org.qbrp.main.engine.assets.contentpacks.build.ContentPackBuildAPI
import java.io.File

@Autoload(LoadPriority.MODULE - 2)
class ContentPackManager : QbModule("resourcepack-versions"), ContentPackManagerAPI {
    companion object {
        val CONTENTPACKS_PATH = getOrCreate(FileSystem.HTTP_SERVER_ASSETS.resolve("contentpacks"), true)
        val CONTENTPACKS_PATCHES_PATH = getOrCreate(FileSystem.HTTP_SERVER_ASSETS.resolve("contentpacks-patches"), true)
    }

    init {
        dependsOn { Engine.isApiAvailable<PatchesAPI>() }
        dependsOn { Engine.isApiAvailable<ContentPackBuildAPI>() }
    }

    private lateinit var patchesAPI: PatchesAPI
    private lateinit var buildAPI: ContentPackBuildAPI
    private lateinit var repository: VersionRepository
    private var version: String = "3.0.0"

    override fun getKoinModule() = inner<ContentPackManagerAPI>(this) {
        scoped { VersionRepository(CONTENTPACKS_PATH, CONTENTPACKS_PATCHES_PATH)  }
    }

    override fun onEnable() {
        get<ServerInfoAPI>().COMPOSER.component(CONTENTPACKS_ENABLED, true)

        patchesAPI = get()
        buildAPI = get()
        repository = getLocal()

        createVersionEntry(version).apply {
            createManifest()
            buildContentPack()
            zipUp()
        }
        generatePatchesToVersion(version)
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

    override fun createVersionEntry(version: String): VersionEntry {
        val versionEntry = repository.getVersion(version)
        return versionEntry
    }

    override fun createPatch(oldVersion: String, newVersion: String): Patch {
        val oldVer = repository.getVersion(oldVersion)
        val newVer = repository.getVersion(newVersion)
        return Patch(oldVer, newVer, patchesAPI, CONTENTPACKS_PATCHES_PATH)
    }

    override fun getLatestVersionEntry(): File {
        return repository.getVersion(version).file
    }

    override fun isPatchExists(oldVersion: String, newVersion: String): Boolean {
        return repository.getPatch(oldVersion, newVersion).exists()
    }

    override fun getPacksFile(): File = CONTENTPACKS_PATH
    override fun getPatchesFile(): File = CONTENTPACKS_PATCHES_PATH
    override fun getVersion(): String = version
}