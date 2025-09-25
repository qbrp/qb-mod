package org.qbrp.client.render.ui.components.event

interface MouseRelease {
    fun onMouseRelease(lmX: Float, lmY: Float, button: Int): Boolean
}