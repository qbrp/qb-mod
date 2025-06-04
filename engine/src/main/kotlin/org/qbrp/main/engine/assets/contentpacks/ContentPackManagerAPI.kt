package org.qbrp.main.engine.assets.contentpacks

import org.qbrp.main.core.modules.ModuleAPI
import java.io.File

interface ContentPackManagerAPI: ModuleAPI {
    fun getVersion(): String
    fun setVersion(version: String)
    fun getLatestVersionEntry(): File
    fun getPacksFile(): File
    fun getPatchesFile(): File
    fun isPatchExists(oldVersion: String, newVersion: String): Boolean
    fun createVersionEntry(version: String): VersionEntry
    fun createPatch(oldVersion: String, newVersion: String): Patch
    fun generatePatchesToVersion(version: String)
}