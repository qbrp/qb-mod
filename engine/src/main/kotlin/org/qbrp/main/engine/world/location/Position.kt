package org.qbrp.main.engine.world.location

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.engine.physics.dynamics.ChunkConstants
import org.qbrp.main.engine.physics.dynamics.dataformats.QuatAdapter
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.TransformAdapter
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import kotlin.math.sqrt

interface Position {
    val x: Double
    val y: Double
    val z: Double

    fun length(): Double {
        return sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    fun normalize(): Vec3Adapter {
        val d = sqrt(this.x * this.x + this.y * this.y + this.z * this.z)
        return if (d < 1.0E-4) Vec3Adapter() else Vec3Adapter(this.x / d, this.y / d, this.z / d)
    }

    fun lerp(pos: Position, alpha: Double = 1.0): Position {
        return Vec3Adapter(x + (pos.x - x) * alpha, y + (pos.y - y) * alpha, z + (pos.z - z) * alpha)
    }

    fun mul(pos: Position): Vec3Adapter {
        return Vec3Adapter(x * pos.x, y * pos.y, z * pos.z)
    }

    fun mul(scale: Double): Vec3Adapter {
        return Vec3Adapter(x * scale, y * scale, z * scale)
    }

    fun add(x: Double, y: Double, z: Double, t: Double = 1.0): Position {
        return Vec3Adapter(x + this.x * t, y + this.y * t, z + this.z * t)
    }

    fun add(other: Position, t: Double = 1.0): Position {
        return add(other.x, other.y, other.z, t)
    }

    fun sub(other: Position): Position {
        return Vec3Adapter(x - other.x, y - other.y, z - other.z)
    }

    fun distanceTo(position: Position): Double {
        val d: Double = position.x - this.x
        val e: Double = position.y - this.y
        val f: Double = position.z - this.z
        return sqrt(d * d + e * e + f * f)
    }

    fun toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())
    fun minecraft(): Vec3d = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
    fun isInsideChunk(chunkX: Int, chunkZ: Int): Boolean {
        val chunkMinX = chunkX shl 4
        val chunkMinZ = chunkZ shl 4

        return x >= chunkMinX && x < chunkMinX + 16 && z >= chunkMinZ && z < chunkMinZ + 16
    }
    fun isInsideChunk(chunk: Chunk): Boolean {
        return isInsideChunk(chunk.pos.x, chunk.pos.z)
    }
    fun toLocal(chunkPos: ChunkPos): Position {
        val baseX = chunkPos.x * ChunkConstants.CHUNK_SIZE
        val baseZ = chunkPos.z * ChunkConstants.CHUNK_SIZE
        val localX = this.x - baseX
        val localZ = this.z - baseZ
        val localY = this.y
        return Vec3Adapter(localX, localY, localZ)
    }
    fun toTransform(): Transform {
        return TransformAdapter(this, QuatAdapter(0.0, 0.0, 0.0, 1.0))
    }
    fun squaredDistanceTo(pos: Position): Double {
        val dx = x - pos.x
        val dy = y - pos.y
        val dz = z - pos.z
        return dx * dx + dy * dy + dz * dz
    }
}