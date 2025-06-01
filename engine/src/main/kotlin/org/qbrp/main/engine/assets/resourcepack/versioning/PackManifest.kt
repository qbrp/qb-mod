package org.qbrp.main.engine.assets.resourcepack.versioning

import kotlinx.serialization.Serializable
import org.qbrp.main.core.assets.common.Asset

@Serializable
data class PackManifest(val version: String): Asset() {
}