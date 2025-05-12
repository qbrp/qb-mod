package org.qbrp.engine.assets.resourcepack.versioning

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import org.qbrp.core.assets.Assets
import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.FileSystem.getOrCreate
import org.qbrp.engine.Engine
import org.qbrp.engine.assets.patches.Manifest
import org.qbrp.engine.assets.patches.PatchesAPI
import org.qbrp.engine.assets.resourcepack.baking.ResourcePackAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.modules.QbModule
import java.io.File

@Autoload(0)
class ResourcePackVersionsModule: QbModule("resourcepack-versions"), ResourcePackVersionsAPI {
    companion object {
        val RESOURCEPACKS_PATH = getOrCreate("qbrp/temp/pack-patches", true)
    }
    private lateinit var resourcePackAPI: ResourcePackAPI
    private lateinit var patchesApi: PatchesAPI
    private var lastVersion: String = "1.0.0"

    init {
        dependsOn { Engine.isApiAvailable<PatchesAPI>() }
        dependsOn { Engine.isApiAvailable<ResourcePackAPI>() }
    }

    override fun getAPI(): ResourcePackVersionsAPI = this

    override fun load() {
        //createResourcePackEntry("1.0.0")
        createResourcePackEntry("2.0.0")
        createPatch("1.0.0", "2.0.0")
        generateZip("1.0.0-2.0.0")
        generateZip("2.0.0")
    }

    override fun getLatestVersionFile(): File {
        return getVersionFile(lastVersion)
    }

    override fun getPacksFile(): File {
        return RESOURCEPACKS_PATH
    }

    override fun getLatestVersion(): String {
        return lastVersion
    }

    override fun isPatchExists(oldVersion: String, newVersion: String): Boolean {
        return getVersionFile("$oldVersion-$newVersion").exists()
    }

    fun generateZip(version: String): File {
        val toZipDir = getVersionFile(version).resolve("zip")
        val zipFilePath = getVersionFile(version).resolve("pack.zip")
        if (zipFilePath.exists()) {
            zipFilePath.delete()
        }
        FileSystem.zipDirectoryTo(zipFilePath, toZipDir.toPath())
        return zipFilePath
    }

    fun getVersionFile(version: String): File {
        return RESOURCEPACKS_PATH.resolve(version)
    }

    fun createResourcePackEntry(version: String) {
        resourcePackAPI = requireApi<ResourcePackAPI>()
        patchesApi = requireApi<PatchesAPI>()
        val versionFile = getVersionFile(version)
        val zipOutputFile = versionFile.resolve("zip")
        val packFile = zipOutputFile.resolve("pack")
        val manifestFile = versionFile.resolve("manifest.json")
        resourcePackAPI.bakeResourcePack(packFile)
        val manifest = patchesApi.generateManifest(zipOutputFile, version)
        val packManifest = PackManifest(version)
        zipOutputFile.resolve("manifest.json").writeText(ObjectMapper().writeValueAsString(packManifest))
        getOrCreate(manifestFile).writeText(Json.encodeToString(manifest))
    }

    fun createPatch(oldVersion: String, newVersion: String) {
        createResourcePackEntry("temp")
        val oldVersionFile = getVersionFile(oldVersion)
        val newVersionFile = RESOURCEPACKS_PATH.resolve("$oldVersion-$newVersion")
        val tempVersionFile = RESOURCEPACKS_PATH.resolve("temp")
        val oldManifest = Json.decodeFromString<Manifest>(oldVersionFile.resolve("manifest.json").readText())
        val newManifest = Json.decodeFromString<Manifest>(tempVersionFile.resolve("manifest.json").readText())
        val diff = oldManifest.diff(newManifest)
        val zipOutputFile = newVersionFile.resolve("zip")
        val tempZipOutputFile = tempVersionFile.resolve("zip")
        patchesApi.generateChangesPatch(tempZipOutputFile, zipOutputFile, diff)
        zipOutputFile.resolve("diff.json").writeText(Json.encodeToString(diff))
        //tempVersionFile.deleteRecursively()
    }

}