package org.qbrp.engine.assets.resourcepack.versioning

import org.qbrp.core.assets.common.Asset

data class PackManifest(val version: String): Asset("manifest") {
}