package org.qbrp.main.engine.assets.resourcepack

import kotlinx.serialization.Serializable

@Serializable
data class PackMcMeta(val pack: Pack) {
    @Serializable
    data class Pack(val description: String, val pack_format: Int)
}