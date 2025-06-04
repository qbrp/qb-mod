package org.qbrp.main.engine.assets.build

import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.assets.contentpacks.ContentPackManagerAPI

class BuildApi {
    private val versionsModule = Engine.getAPI<ContentPackManagerAPI>()!!
    val contentPacks
        get() = versionsModule.getPacksFile()
    val versionFile
        get() = versionsModule.getLatestVersionEntry()
    val version
        get() = versionsModule.getVersion()

    fun isPatchExists(oldVersion: String, newVersion: String) = versionsModule.isPatchExists(oldVersion, newVersion)
    fun bakeResourcePack(version: String) = versionsModule.createVersionEntry(version)
    fun addPatch(oldVersion: String, newVersion: String) = versionsModule.createPatch(oldVersion, newVersion)
}