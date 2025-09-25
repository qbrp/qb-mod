package org.qbrp.client.render

import org.qbrp.client.core.EngineId
import org.qbrp.main.ApplicationLayer

object Render: ApplicationLayer("org.qbrp.client.render") {
    val DRAG_N_DROP = EngineId("textures/gui/icons/drag_n_drop.png")
    val DOT = EngineId("textures/gui/icons/dot.png")
    val WHITE_FILL = EngineId("textures/gui/white_fill.png")
    val INFO = EngineId("textures/gui/icons/info.png")

    const val ICON_TEXTURES_PATH = "textures/gui/icons"
}