package org.qbrp.client.render.dataformats

data class MutableVec2(override var x: Float, override var y: Float): Vec2 {
    override fun scale(factor: Float): Vec2 {
        x *= factor
        y * factor
        return this
    }

    override fun divide(factor: Float): Vec2 {
        x /= factor
        y /= factor
        return this
    }

    override fun add(x: Float, y: Float): Vec2 {
        this.x + x
        this.y + y
        return this
    }

    companion object {
        fun of(vec2: Vec2) = MutableVec2(vec2.x, vec2.y)
    }
}