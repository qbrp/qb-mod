package org.qbrp.main.core.util.math

import kotlin.math.abs
import kotlin.math.acos

data class QuatImmutable(
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val w: Double,
) : Quat

fun Quat(x: Double, y: Double, z: Double, w: Double) = QuatImmutable(x, y, z, w)

fun QuatIdentity() = QuatImmutable(0.0, 0.0, 0.0, 1.0)

interface Quat {
    val x: Double
    val y: Double
    val z: Double
    val w: Double
}

fun angleBetween(a: Quat, b: Quat): Double {
    val dot = (a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w)
        .coerceIn(-1.0, 1.0)
    return (2.0 * acos(abs(dot)))
}