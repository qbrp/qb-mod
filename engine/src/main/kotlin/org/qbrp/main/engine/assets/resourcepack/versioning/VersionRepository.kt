package org.qbrp.main.engine.assets.resourcepack.versioning

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File

class VersionRepository(
    private val baseDir: File,
    private val patchesDir: File
): KoinComponent {
    fun listVersions(): List<String> =
        baseDir.listFiles()?.map { it.name }?.filter { it != "temp" } ?: emptyList()

    fun getVersion(version: String): ContentPackVersion =
        ContentPackVersion(version, baseDir, get(), get())

    fun getPatch(old: String, new: String): File =
        patchesDir.resolve("$old-$new")
}
