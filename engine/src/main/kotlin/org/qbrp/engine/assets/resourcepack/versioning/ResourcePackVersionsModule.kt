package org.qbrp.engine.assets.resourcepack.versioning

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.FileSystem.getOrCreate
import org.qbrp.engine.Engine
import org.qbrp.engine.assets.patches.Manifest
import org.qbrp.engine.assets.patches.PatchesAPI
import org.qbrp.engine.assets.resourcepack.baking.ResourcePackAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.InfoNames
import org.qbrp.system.networking.ServerInformation
import java.io.File

@Autoload(0)
class ResourcePackVersionsModule: QbModule("resourcepack-versions"), ResourcePackVersionsAPI {
    companion object {
        val CONTENTPACKS_PATH = getOrCreate(FileSystem.HTTP_SERVER_ASSETS.resolve("contentpacks"), true)
        val CONTENTPACKS_PATCHES_PATH = getOrCreate(FileSystem.HTTP_SERVER_ASSETS.resolve("contentpacks-patches"), true)
    }
    private lateinit var resourcePackAPI: ResourcePackAPI
    private lateinit var patchesApi: PatchesAPI
    private var version: String = "2.0.0"

    init {
        dependsOn { Engine.isApiAvailable<PatchesAPI>() }
        dependsOn { Engine.isApiAvailable<ResourcePackAPI>() }
    }

    override fun getAPI(): ResourcePackVersionsAPI = this

    override fun onLoad() {
        ServerInformation.COMPOSER.component(InfoNames.CONTENTPACKS_ENABLED, true)
        createVersion("2.0.0")
        //generateZip(getVersionFile("2.0.0"))
        //generatePatchesToVersion("2.0.0")
    }

    fun createVersion(version: String) {
        createResourcePackEntry(version).also {
            generateZip(it)
        }
    }

    fun generatePatchesToVersion(version: String) {
        CONTENTPACKS_PATH.listFiles().forEach {
            val name = it.name
            if (name != version && name != "temp") {
                createPatch(name, version).also {
                    generateZip(it)
                }
            }
        }
    }

    override fun getLatestVersionFile(): File {
        return getVersionFile(version)
    }

    override fun getPacksFile(): File {
        return CONTENTPACKS_PATH
    }

    override fun getPatchesFile(): File {
        return CONTENTPACKS_PATCHES_PATH
    }

    override fun getVersion(): String {
        return version
    }

    override fun isPatchExists(oldVersion: String, newVersion: String): Boolean {
        return getPatchFile(oldVersion, newVersion).exists()
    }

    fun generateZip(version: File): File {
        val toZipDir = version.resolve("zip")
        val zipFilePath = version.resolve("pack.zip")
        if (zipFilePath.exists()) {
            zipFilePath.delete()
        }
        FileSystem.zipDirectoryTo(zipFilePath, toZipDir.toPath())
        return zipFilePath
    }

    fun getVersionFile(version: String): File {
        return CONTENTPACKS_PATH.resolve(version)
    }

    fun getPatchFile(oldVersion: String, newVersion: String): File {
        return CONTENTPACKS_PATCHES_PATH.resolve("$oldVersion-$newVersion")
    }

    override fun createResourcePackEntry(version: String): File {
        resourcePackAPI = requireApi<ResourcePackAPI>()
        patchesApi = requireApi<PatchesAPI>()
        val versionFile = getVersionFile(version)
        val zipOutputFile = versionFile.resolve("zip")
        val packFile = zipOutputFile.resolve("qbrp-pack")
        val manifestFile = versionFile.resolve("manifest.json")
        resourcePackAPI.bakeResourcePack(packFile)
        val manifest = patchesApi.generateManifest(zipOutputFile, version)
        val packManifest = PackManifest(version)
        zipOutputFile.resolve("manifest.json").writeText(ObjectMapper().writeValueAsString(packManifest))
        getOrCreate(manifestFile).writeText(Json.encodeToString(manifest))
        return versionFile
    }

    override fun createPatch(oldVersion: String, newVersion: String): File {
        createResourcePackEntry("temp")
        val oldVersionFile = getVersionFile(oldVersion)
        val newVersionFile = CONTENTPACKS_PATCHES_PATH.resolve("$oldVersion-$newVersion")
        val tempVersionFile = CONTENTPACKS_PATH.resolve("temp")
        val oldManifest = Json.decodeFromString<Manifest>(oldVersionFile.resolve("manifest.json").readText())
        val newManifest = Json.decodeFromString<Manifest>(tempVersionFile.resolve("manifest.json").readText())
        val diff = oldManifest.diff(newManifest)
        val zipOutputFile = newVersionFile.resolve("zip")
        val tempZipOutputFile = tempVersionFile.resolve("zip")
        patchesApi.generateChangesPatch(tempZipOutputFile, zipOutputFile, diff)
        zipOutputFile.resolve("diff.json").writeText(Json.encodeToString(diff))
        tempVersionFile.deleteRecursively()
        return newVersionFile
    }

}