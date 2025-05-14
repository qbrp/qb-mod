package org.qbrp.engine.assets.resourcepack.baking

import kotlinx.serialization.Serializable

@Serializable
data class PackMcMeta(val pack: Pack) {
    @Serializable
    data class Pack(val description: String, val pack_format: String = "56")
}