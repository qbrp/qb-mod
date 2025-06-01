package org.qbrp.client.engine.contentpacks

import kotlinx.serialization.Transient
import org.qbrp.main.core.assets.common.NamedAsset

data class DownloadConfig(val host: String = "0.0.0.0", val port: Int = 25008, val serverName: String = "LOCAL"): NamedAsset() {
    @Transient override val name: String = "config"
}