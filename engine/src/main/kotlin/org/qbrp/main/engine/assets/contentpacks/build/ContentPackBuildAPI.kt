package org.qbrp.main.engine.assets.contentpacks.build

import org.qbrp.main.engine.assets.contentpacks.VersionEntry
import java.io.File

interface ContentPackBuildAPI {
    fun build(file: File, version: String): ContentPack
    fun build(entry: VersionEntry): ContentPack = build(entry.contentPackDir, entry.version)
}