package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.Quat
import com.github.stephengold.joltjni.RVec3
import com.github.stephengold.joltjni.Vec3
import com.github.stephengold.joltjni.readonly.Vec3Arg
import org.joml.Quaternionf
import org.joml.Vector3f
import org.qbrp.main.core.util.math.Vec3 as EngineVec3
import org.qbrp.main.core.util.math.Quat as EngineQuat

fun EngineVec3.jolt(): Vec3 = Vec3(x, y, z)
fun EngineVec3.joltR(): RVec3 = RVec3(x, y, z)
fun EngineVec3.joml(): Vector3f = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun EngineQuat.jolt(): Quat = Quat(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun EngineQuat.joml(): Quaternionf = Quaternionf(x, y, z, w)

fun Vec3Arg.engine() = EngineVec3(x.toDouble(), y.toDouble(), z.toDouble())
fun RVec3.engine() = EngineVec3((x as Float).toDouble(), (y as Float).toDouble(), (z as Float).toDouble())
fun Quat.engine() = EngineQuat(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun Quaternionf.engine() = EngineQuat(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

fun EngineVec3(x: Double, y: Double, z: Double) = EngineVec3(x, y, z)