package org.qbrp.engine.assets.resourcepack.baking

import kotlinx.serialization.Serializable

@Serializable
data class DisplayConfig(
    val gui: ModelDisplay? = null,
    val ground: ModelDisplay? = null,
    val fixed: ModelDisplay? = null,
    val thirdperson_righthand: ModelDisplay? = null,
    val firstperson_righthand: ModelDisplay? = null,
    val firstperson_lefthand: ModelDisplay? = null
) {

    @Serializable
    data class ModelDisplay(
        val rotation: List<Float>,
        val translation: List<Float>,
        val scale: List<Float>
    )
}