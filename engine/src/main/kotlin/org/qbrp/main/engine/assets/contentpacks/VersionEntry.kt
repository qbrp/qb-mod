package org.qbrp.main.engine.assets.contentpacks

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.engine.assets.contentpacks.build.ContentPackBuildAPI
import org.qbrp.main.engine.assets.contentpacks.patches.PatchesAPI
import java.io.File

class VersionEntry(
    val version: String,
    val file: File,
    private val patchesApi: PatchesAPI
) {
    val contentPackDir = file.resolve("zip")
    val manifestFile = file.resolve("manifest.json")

    fun buildContentPack(buildApi: ContentPackBuildAPI = GlobalContext.get().get()) {
        buildApi.build(this)
    }

    fun createManifest() {
        val manifest = patchesApi.generateManifest(contentPackDir, version)
        manifestFile.writeText(Json.encodeToString(manifest))
    }

    fun zipUp(): File {
        val zipOut = file.resolve("pack.zip")
        if (zipOut.exists()) { zipOut.delete() }
        FileSystem.zipDirectoryTo(zipOut, contentPackDir.toPath())
        return zipOut
    }
}