package org.qbrp.main.engine.physics.system.entity

import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.Quat
import org.qbrp.main.core.util.math.QuatIdentity
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.ZeroVec3
import org.qbrp.main.core.util.math.asVec3

interface PhysicsPos : Pos

@JvmInline
value class PhysicsVec3(val v: Vec3) : PhysicsPos, Vec3 by v

@JvmInline
value class PhysicsRotation(val q: Quat) : Quat by q

data class Transform(
    val pos: PhysicsPos,
    val rot: PhysicsRotation
) {
    val posVector by lazy { pos.asVec3() }

    companion object {
        val NULL = Transform(
            ZeroVec3().asPhysical(),
            QuatIdentity().asPhysical()
        )
    }
}

fun Transform(pos: Pos) = Transform(pos.asPhysical(), QuatIdentity().asPhysical())

fun Pos.asPhysical() : PhysicsPos = PhysicsVec3(PhysicsVec3(Vec3(x, y, z)))

fun Vec3.asPhysical() = PhysicsVec3(this)

fun Quat.asPhysical() = PhysicsRotation(this)

fun PhysicsRotation.toWorld() = Quat(-x, -y, z, w)