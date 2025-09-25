package org.qbrp.client.render.ui.view

class TransformationContext(val offsetX: Float, val offsetY: Float) {
    fun withOffset(dx: Float, dy: Float): TransformationContext =
        TransformationContext(offsetX + dx, offsetY + dy)

    fun getX(x: Float) = x - offsetX
    fun getY(y: Float) = y - offsetY

    fun getX(x: Int) = (x - offsetX).toInt()
    fun getY(y: Int) = (y - offsetY).toInt()

    fun getXfl(x: Int) = x - offsetX
    fun getYfl(y: Int) = y - offsetY
}