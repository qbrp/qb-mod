package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.ui.view.View

fun interface ClickListener {
    fun onClicked(view: View, button: Int, pos: Vec2): Boolean
}