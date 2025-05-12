package org.qbrp.engine.assets.patches

import kotlinx.serialization.Serializable

@Serializable
data class FileEntry(val path: String, val hash: String) {
}