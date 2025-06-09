package org.qbrp.main.engine.items.components.containers

import kotlinx.serialization.Serializable

@Serializable
data class Dimensions(val lengthCm: Int, val widthCm: Int, val heightCm: Int) {
}