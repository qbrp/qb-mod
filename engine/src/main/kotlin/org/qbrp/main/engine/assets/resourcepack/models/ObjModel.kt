package org.qbrp.main.engine.assets.resourcepack.models

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.assets.resourcepack.DisplayConfig

@Serializable
data class ObjModel(
    override val parent: String,
    override val model: String,
    val display: DisplayConfig? = null,
): Model {
}