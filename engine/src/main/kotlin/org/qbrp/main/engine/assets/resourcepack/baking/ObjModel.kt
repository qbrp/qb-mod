package org.qbrp.main.engine.assets.resourcepack.baking

import kotlinx.serialization.Serializable

@Serializable
data class ObjModel(
    override val parent: String,
    override val model: String,
    val display: DisplayConfig? = null,
): Model {
}