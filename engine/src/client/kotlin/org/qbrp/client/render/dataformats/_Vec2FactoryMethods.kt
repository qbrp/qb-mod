package org.qbrp.client.render.dataformats

fun vec2(x: Float, y: Float): Vec2 = ImmutableVec2(x, y)

fun vec2(v: Vec2): Vec2 = ImmutableVec2(v.x, v.y)

fun vec2(x: Int, y: Int): Vec2 = ImmutableVec2(x.toFloat(), y.toFloat())