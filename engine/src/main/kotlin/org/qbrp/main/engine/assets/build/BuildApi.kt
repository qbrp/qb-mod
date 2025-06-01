package org.qbrp.main.engine.assets.build

import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.assets.resourcepack.versioning.ResourcePackVersionsAPI

class BuildApi {
    private val versionsModule = Engine.getAPI<ResourcePackVersionsAPI>()!!
    val contentPacks
        get() = versionsModule.getPacksFile()
    val versionFile
        get() = versionsModule.getLatestVersionFile()
    val version
        get() = versionsModule.getVersion()

    fun isPatchExists(oldVersion: String, newVersion: String) = versionsModule.isPatchExists(oldVersion, newVersion)
    fun bakeResourcePack(version: String) = versionsModule.createResourcePackEntry(version)
    fun addPatch(oldVersion: String, newVersion: String) = versionsModule.createPatch(oldVersion, newVersion)
}