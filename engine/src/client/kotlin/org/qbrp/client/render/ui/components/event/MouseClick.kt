package org.qbrp.client.render.ui.components.event

interface MouseClick {
    fun onClick(lmX: Float, lmY: Float, button: Int): Boolean
}
