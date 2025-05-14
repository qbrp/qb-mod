package org.qbrp.engine.assets.resourcepack.versioning

import org.qbrp.system.modules.ModuleAPI
import java.io.File

interface ResourcePackVersionsAPI: ModuleAPI {
    fun getVersion(): String
    fun getLatestVersionFile(): File
    fun getPacksFile(): File
    fun getPatchesFile(): File
    fun isPatchExists(oldVersion: String, newVersion: String): Boolean
    fun createResourcePackEntry(version: String): File
    fun createPatch(oldVersion: String, newVersion: String): File
}