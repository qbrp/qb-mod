package org.qbrp.main.core.util.math

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.engine.physics.system.entity.PhysicsPos
import org.qbrp.main.engine.physics.system.entity.PhysicsVec3
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorldVec3
import kotlin.math.min
import kotlin.math.sqrt

private val ZERO = Vec3(0.0, 0.0, 0.0)

fun Vec3d.engine(): Vec3 = Vec3(x, y, z)

fun Vec3d.engineWorldVec3() = engine().asWorldVec3()

fun Vec3.toMinecraft(): Vec3d = Vec3d(x, y, z)

fun Pos.toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())

fun Pos.getChunkPos(): ChunkPos = ChunkPos(toBlockPos())

fun Pos.isInsideChunk(pos: ChunkPos): Boolean {
    return x >= pos.startX && x < pos.endX && z >= pos.startZ && z < pos.endZ + 16
}

fun Pos.isInsideChunk(chunk: Chunk): Boolean {
    return isInsideChunk(chunk.pos)
}

fun ChunkPos.localOf(pos: WorldPos): Vec3 {
    val localX = endX - pos.x
    val localZ = endZ - pos.z
    val localY = pos.y
    return Vec3(localX, localY, localZ)
}

fun Pos.cloneAsVec3(x: Double = this.x, y: Double = this.y, z: Double = this.z) = Vec3(x, y, z)

fun Pos.clonePos(x: Double = this.x, y: Double = this.y, z: Double = this.z): Pos = cloneAsVec3(x, y, z)

interface Pos {
    val x: Double
    val y: Double
    val z: Double
}

fun Pos.asVec3() = Vec3(this.x, this.y, this.z)

fun WorldPos.asVec3() = WorldVec3(Vec3(x, y, z))

fun PhysicsPos.asVec3() = PhysicsVec3(Vec3(x, y, z))

fun WorldVec3.convertToPhysics(): PhysicsVec3 = PhysicsVec3(Vec3(x, y, -z))

fun PhysicsVec3.convertToWorld(): WorldVec3 = WorldVec3(Vec3(x, y, -z))

interface Vec3 : Pos {
    fun length(): Double

    fun normalize(): Vec3

    fun negate(): Vec3 {
        return mul(-1.0)
    }

    fun lerp(vec3: Vec3, alpha: Double = 1.0): Vec3

    fun mul(pos: Vec3): Vec3

    fun mul(scale: Double): Vec3

    fun mul(scale: Int): Vec3 {
        return mul(scale.toDouble())
    }

    fun add(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0, t: Double = 1.0): Vec3

    fun add(other: Vec3, t: Double = 1.0): Vec3 {
        return add(other.x, other.y, other.z, t)
    }

    fun sub(x: Double, y: Double, z: Double): Vec3

    fun sub(other: Vec3): Vec3 {
        return sub(other.x, other.y, other.z)
    }

    fun clampMax(x: Double = this.x, y: Double = this.y, z: Double = this.z): Vec3 {
        return Vec3(
            min(x, this.x),
            min(y, this.y),
            min(z, this.z)
        )
    }

    fun clampMax(value: Double): Vec3 {
        return clampMax(value, value, value)
    }

    fun squaredDistanceTo(to: Pos): Double {
        val dx = x - to.x
        val dy = y - to.y
        val dz = z - to.z
        return dx * dx + dy * dy + dz * dz
    }
}

fun ZeroVec3() = ZERO

fun ZeroWorldVec3() = ZERO.asWorldVec3()

fun Vec3(x: Double, y: Double, z: Double) = Vec3Immutable(x, y, z)

fun Vec3(x: Int, y: Int, z: Int) = Vec3Immutable(x.toDouble(), y.toDouble(), z.toDouble())

data class Vec3Immutable(
    override val x: Double,
    override val y: Double,
    override val z: Double
) : Vec3 {
    override fun length(): Double {
        return sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    override fun normalize(): Vec3 {
        val d = sqrt(this.x * this.x + this.y * this.y + this.z * this.z)
        return if (d < 1.0E-4) ZeroVec3() else Vec3(this.x / d, this.y / d, this.z / d)
    }

    override fun lerp(vec3: Vec3, alpha: Double): Vec3 {
        return Vec3(
            x + (vec3.x - x) * alpha,
            y + (vec3.y - y) * alpha,
            z + (vec3.z - z) * alpha
        )
    }

    override fun mul(pos: Vec3): Vec3 {
        return Vec3(
            x * pos.x,
            y * pos.y,
            z * pos.z
        )
    }

    override fun mul(scale: Double): Vec3 {
        return Vec3(x * scale, y * scale, z * scale)
    }

    override fun add(x: Double, y: Double, z: Double, t: Double): Vec3 {
        return Vec3(x + this.x * t, y + this.y * t, z + this.z * t)
    }

    override fun sub(x: Double, y: Double, z: Double): Vec3 {
        return Vec3(this.x - x, this.y - y, this.z - z)
    }
}