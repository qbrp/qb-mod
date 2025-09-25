package org.qbrp.client.render.ui.components.event

interface Clickable {
    fun onClick(button: Int, lx: Float, ly: Float): Boolean
}