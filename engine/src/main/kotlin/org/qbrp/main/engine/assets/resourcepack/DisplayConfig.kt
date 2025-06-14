package org.qbrp.main.engine.assets.resourcepack

import kotlinx.serialization.Serializable

@Serializable
data class DisplayConfig(
    val gui: ModelDisplay? = null,
    val ground: ModelDisplay? = null,
    val fixed: ModelDisplay? = null,
    val thirdperson_righthand: ModelDisplay? = null,
    val thirdperson_lefthand: ModelDisplay? = null,
    val firstperson_righthand: ModelDisplay? = null,
    val firstperson_lefthand: ModelDisplay? = null
) {

    @Serializable
    data class ModelDisplay(
        val rotation: List<Float> = listOf(0f, 0f, 0f),
        val translation: List<Float> = listOf(0f, 0f, 0f),
        val scale: List<Float> = listOf(0f, 0f, 0f)
    )
}