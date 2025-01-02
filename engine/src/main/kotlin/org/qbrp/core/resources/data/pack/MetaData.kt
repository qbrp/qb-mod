package org.qbrp.core.resources.data.pack

import org.qbrp.core.resources.data.Data
import org.qbrp.core.resources.data.config.ServerConfigData.Resources.Pack

data class MetaData(
    val pack: Pack
) : Data() {
    override fun toFile(): String = gson.toJson(this)
}
