package org.qbrp.main.engine.assets.contentpacks.patches

import kotlinx.serialization.Serializable

@Serializable
data class FileEntry(val path: String, val hash: String) {
}