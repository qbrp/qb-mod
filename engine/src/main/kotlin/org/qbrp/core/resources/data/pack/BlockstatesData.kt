package org.qbrp.core.resources.data.pack

import org.qbrp.core.resources.data.Data

data class BlockstatesData(val variants: MutableMap<String, BlockModel>): Data() {
    override fun toFile(): String = gson.toJson(this)

    data class BlockModel(
        val model: String
    )
}