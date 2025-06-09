package org.qbrp.main.engine.assets.contentpacks.build

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.qbrp.main.core.assets.common.Asset
import java.io.File

@Serializable
data class ModelsList(val ids: Map<String, String>): Asset() {
    fun create(file: File) {
        file.writeText(Json.encodeToString(this))
    }
    fun toModelEntry(): List<ModelEntry> {
        return ids.map { (k, v) -> ModelEntry(k, v) }
    }
}