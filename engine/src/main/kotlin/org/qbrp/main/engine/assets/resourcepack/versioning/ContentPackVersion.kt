package org.qbrp.main.engine.assets.resourcepack.versioning

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.engine.assets.patches.PatchesAPI
import org.qbrp.main.engine.assets.resourcepack.baking.ResourcePackAPI
import java.io.File

class ContentPackVersion(
    val version: String,
    val baseDir: File,
    private val resourcePackAPI: ResourcePackAPI,
    private val patchesApi: PatchesAPI
) {
    private val versionDir = baseDir.resolve(version)
    val zipDir = versionDir.resolve("zip")
    val manifestFile = versionDir.resolve("manifest.json")

    fun bake() {
        val packFile = zipDir.resolve("qbrp-pack")
        resourcePackAPI.bakeResourcePack(packFile)
        // 1. внутренний манифест
        val internalManifest = PackManifest(version)
        zipDir.resolve("manifest.json")
            .writeText(Json.encodeToString(internalManifest))
        val externalManifest = patchesApi.generateManifest(zipDir, version)
        manifestFile.writeText(Json.encodeToString(externalManifest))
    }

    fun zipUp(): File {
        val zipOut = versionDir.resolve("pack.zip")
        if (zipOut.exists()) { zipOut.delete() }
        FileSystem.zipDirectoryTo(zipOut, zipDir.toPath())
        return zipOut
    }
}