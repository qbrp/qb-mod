package org.qbrp.client.render.ui.inventory.entity

import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.main.network.inventory.ScreenCorner

val ScreenCorner.anchorsPos get() = when(this) {
    ScreenCorner.TOP_LEFT -> vec2(0.05f, 0.05f)
    ScreenCorner.BOTTOM_LEFT -> vec2(0.05f, 0.95f)
}

val ScreenCorner.pivot get() = when(this) {
    ScreenCorner.TOP_LEFT -> Pivot(0f, 0f)
    ScreenCorner.BOTTOM_LEFT -> Pivot(0f, 1f)
}