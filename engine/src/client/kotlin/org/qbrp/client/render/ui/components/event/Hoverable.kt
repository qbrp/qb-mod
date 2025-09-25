package org.qbrp.client.render.ui.components.event

interface Hoverable {
    fun onHoverEnter() {}
    fun onHoverExit() {}
    fun onHover(x: Float, y: Float) {}
}