package org.qbrp.client.render.ui.view

import org.qbrp.client.render.ui.UiContext

enum class HitBehavior { OPAQUE, TRANSLUCENT, IGNORE }

enum class HitResult { NONE, SELF, DESCENDANT }

context(ui: UiContext)
fun View.isHovered(gap: Float = hoverGap): Boolean {
    val (mgx, mgy) = ui.mouse.pos.x to ui.mouse.pos.y
    val (vgx, vgy) = globalPos.x to globalPos.y
    return mgx in vgx - gap..vgx + width + gap && mgy in vgy - gap..vgy + height + gap
}
