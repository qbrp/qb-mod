package org.qbrp.client.render.dataformats

interface Vec2 {
    val x: Float
    val y: Float
    fun scale(factor: Float): Vec2
    fun divide(factor: Float): Vec2
    fun add(x: Float, y: Float): Vec2
    fun add(vec2: Vec2): Vec2 {
        return add(vec2.x, vec2.y)
    }
}
