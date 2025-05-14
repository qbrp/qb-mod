package org.qbrp.engine.client.engine.contentpacks

import org.qbrp.core.assets.common.NamedAsset

data class DownloadConfig(val host: String = "0.0.0.0", val port: Int = 25008, val serverName: String = "LOCAL"): NamedAsset() {
    override val name: String = "config"
}