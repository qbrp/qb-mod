package org.qbrp.main.engine.physics.dynamics.dataformats

import com.github.stephengold.joltjni.Quat
import com.github.stephengold.joltjni.RVec3
import com.github.stephengold.joltjni.Vec3
import com.github.stephengold.joltjni.readonly.Vec3Arg
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import org.qbrp.main.engine.world.location.Position

fun Position.jolt(): Vec3 = Vec3(x, y, z)
fun Position.joltR(): RVec3 = RVec3(x, y, z)

fun org.qbrp.main.engine.physics.dynamics.dataformats.Quat.jolt(): Quat = Quat(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun org.qbrp.main.engine.physics.dynamics.dataformats.Quat.joml(): Quaternionf = Quaternionf(x, y, z, w)

fun Position.joml(): Vector3f = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vec3Arg.engine(): Vec3Adapter = Vec3Adapter(x.toDouble(), y.toDouble(), z.toDouble())
fun RVec3.engine(): Position = Vec3Adapter((x as Float).toDouble(), (y as Float).toDouble(), (z as Float).toDouble())
fun Vec3d.engine(): Position = Vec3Adapter(x, y, z)
fun Quat.engine(): org.qbrp.main.engine.physics.dynamics.dataformats.Quat = QuatAdapter(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun Quaternionf.engine(): org.qbrp.main.engine.physics.dynamics.dataformats.Quat = QuatAdapter(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())