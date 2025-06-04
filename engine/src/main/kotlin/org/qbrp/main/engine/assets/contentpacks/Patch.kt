package org.qbrp.main.engine.assets.contentpacks

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.engine.assets.contentpacks.patches.Manifest
import org.qbrp.main.engine.assets.contentpacks.patches.PatchesAPI
import java.io.File

class Patch(
    private val oldVersion: VersionEntry,
    private val newVersion: VersionEntry,
    private val patchesApi: PatchesAPI,
    private val patchesBaseDir: File
) {
    private val targetDir = patchesBaseDir.resolve("${oldVersion.version}-${newVersion.version}")

    fun create(): File {
        val oldManifest = Json.decodeFromString<Manifest>(
            oldVersion.manifestFile.readText()
        )
        val newManifest = Json.decodeFromString<Manifest>(
            newVersion.manifestFile.readText()
        )

        val diff = oldManifest.diff(newManifest)

        val zipOut = targetDir.resolve("zip")
        patchesApi.generateChangesPatch(newVersion.contentPackDir, zipOut, diff)

        FileSystem.getOrCreate(zipOut.resolve("diff.json")).writeText(Json.encodeToString(diff))

        return targetDir
    }
}