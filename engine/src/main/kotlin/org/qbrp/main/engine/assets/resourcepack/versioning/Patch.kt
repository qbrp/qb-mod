package org.qbrp.main.engine.assets.resourcepack.versioning

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.qbrp.main.engine.assets.patches.Manifest
import org.qbrp.main.engine.assets.patches.PatchesAPI
import java.io.File

class Patch(
    private val oldVersion: ContentPackVersion,
    private val newVersion: ContentPackVersion,
    private val patchesApi: PatchesAPI,
    private val patchesBaseDir: File
) {
    private val targetDir = patchesBaseDir.resolve("${oldVersion.version}-${newVersion.version}")

    fun create(): File {
        // 1) Читаем существующие manifests:
        val oldManifest = Json.decodeFromString<Manifest>(
            oldVersion.manifestFile.readText()
        )
        val newManifest = Json.decodeFromString<Manifest>(
            newVersion.manifestFile.readText()
        )

        // 2) Получаем diff:
        val diff = oldManifest.diff(newManifest)

        // 3) Генерируем патч из готового zip новой версии:
        val zipOut = targetDir.resolve("zip")
        patchesApi.generateChangesPatch(newVersion.zipDir, zipOut, diff)

        // 4) Пишем diff.json:
        zipOut.resolve("diff.json").writeText(Json.encodeToString(diff))

        return targetDir
    }
}