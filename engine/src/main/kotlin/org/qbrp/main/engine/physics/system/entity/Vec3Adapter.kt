package org.qbrp.main.engine.physics.system.entity

import org.qbrp.main.core.util.math.Vec3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun degToRad(deg: Double): Double {
    return deg * (PI / 180.0)
}

fun vec3FromAngle2(angle: Double, y: Double = 0.0, length: Double = 1.0): Vec3 {
    val rad = degToRad(angle)
    return Vec3(sin(rad) * length, y, cos(rad) * length)
}