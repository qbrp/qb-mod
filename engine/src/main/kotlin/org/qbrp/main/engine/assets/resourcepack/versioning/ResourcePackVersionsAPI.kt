package org.qbrp.main.engine.assets.resourcepack.versioning

import org.qbrp.main.core.modules.ModuleAPI
import java.io.File

interface ResourcePackVersionsAPI: ModuleAPI {
    fun getVersion(): String
    fun getLatestVersionFile(): File
    fun getPacksFile(): File
    fun getPatchesFile(): File
    fun isPatchExists(oldVersion: String, newVersion: String): Boolean
    fun createResourcePackEntry(version: String): ContentPackVersion
    fun createPatch(oldVersion: String, newVersion: String): Patch
}