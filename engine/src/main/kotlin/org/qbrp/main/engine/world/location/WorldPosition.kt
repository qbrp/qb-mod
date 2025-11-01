package org.qbrp.main.engine.world.location

import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.ZeroVec3
import org.qbrp.main.core.util.math.ZeroWorldVec3
import org.qbrp.main.core.util.math.asVec3

interface WorldPos : Pos

@JvmInline
value class WorldVec3(val v: Vec3 = ZeroVec3()) : WorldPos, Vec3 by v

fun ZeroWorldPos(): WorldPos = ZeroWorldVec3()

fun Pos.asWorldVec3() = WorldVec3(asVec3())

fun Pos.asWorld(): WorldPos = asWorldVec3()
