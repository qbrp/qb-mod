package org.qbrp.client.render.dataformats

data class ImmutableVec2(override val x: Float, override val y: Float): Vec2 {
    override fun scale(factor: Float) = ImmutableVec2(x * factor, y * factor)
    override fun divide(factor: Float) = ImmutableVec2(x / factor, y / factor)
    override fun add(x: Float, y: Float): Vec2 = ImmutableVec2(this.x + x, this.y + y)
}