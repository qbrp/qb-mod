package org.qbrp.main.engine.assets.contentpacks.versioning

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.qbrp.main.core.assets.common.Asset
import java.io.File

@Serializable
data class PackManifest(val version: String): Asset() {
    fun create(file: File) {
        file.writeText(Json.encodeToString(this))
    }
}