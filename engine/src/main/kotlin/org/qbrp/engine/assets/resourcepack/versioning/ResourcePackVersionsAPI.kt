package org.qbrp.engine.assets.resourcepack.versioning

import org.qbrp.system.modules.ModuleAPI
import java.io.File

interface ResourcePackVersionsAPI: ModuleAPI {
    fun getLatestVersionFile(): File
    fun getPacksFile(): File
    fun getLatestVersion(): String?
    fun isPatchExists(oldVersion: String, newVersion: String): Boolean
}