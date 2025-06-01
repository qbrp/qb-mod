package org.qbrp.main.engine.assets.resourcepack.baking

import kotlinx.serialization.Serializable

@Serializable
data class JsonModel(
    override val parent: String,
    override val model: String,
    val textures: Map<String, String>
): Model {
}