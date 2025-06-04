package org.qbrp.main.engine.assets.contentpacks

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.assets.FileSystem
import java.io.File

class VersionRepository(
    private val baseDir: File,
    private val patchesDir: File
): KoinComponent {
    fun listVersions(): List<String> =
        baseDir.listFiles()?.map { it.name }?.filter { it != "temp" } ?: emptyList()

    fun getVersion(version: String): VersionEntry =
        VersionEntry(version, FileSystem.getOrCreate(baseDir.resolve(version), true), get())

    fun getPatch(old: String, new: String): File =
        patchesDir.resolve("$old-$new")
}
