package org.qbrp.main.engine.physics.chunk

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import org.qbrp.main.core.util.math.QuatIdentity
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.engine.physics.system.entity.MutableCompoundBody
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.entity.asPhysical
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager

@JvmInline
value class ChunkSegments(val shape: Shape.SegmentCompound)

data class ChunkPart(val startY: Int, val endY: Int, val segmentsShape: ChunkSegments) {
    val segmentsList get() = segmentsShape.shape.segments
}

data class ChunkCollider(val body: MutableCompoundBody, val parts: MutableList<ChunkPart>) {
    val shape get() = body.shape
}

data class PhysicsChunkPos(val x: Int, val z: Int)

fun ChunkPos.asPhysics() = PhysicsChunkPos(x, z)

fun BlockPos.getPhysicsChunkPos() = ChunkPos(this).asPhysics()

fun WorldBodyManager.removeChunk(c: PhysicsChunk) = c.collider.body.let { destroyBody(it.id) }

fun WorldBodyManager.addChunk(c: PhysicsChunk) = c.collider.body.let { addBody(it, activate = true) }

fun ChunkTransform(pos: PhysicsChunkPos, y: Int = 0): Transform {
    val mcChunkPos = ChunkPos(pos.x, pos.z)
    val transformPos = Vec3(mcChunkPos.startX, y, mcChunkPos.startZ).asPhysical()
    return Transform(transformPos, QuatIdentity().asPhysical())
}

/**
 * @param minY Отметка высоты начала прогруженной зоны
 * @param maxY Высота прогруженной зоны
 **/
class PhysicsChunk(
    val collider: ChunkCollider,
    val pos: PhysicsChunkPos
) {
    val minY get() = collider.parts.maxOf { it.startY }
    val maxY get() = collider.parts.maxOf { it.endY }

    fun partOfY(y: Int): ChunkPart? {
        return collider.parts.firstOrNull { y in it.startY..it.endY }
    }

    fun partIndexOf(part: ChunkPart): Int {
        return collider.parts.indexOf(part)
    }
}

fun EmptyPhysicsChunk(body: MutableCompoundBody, pos: PhysicsChunkPos): PhysicsChunk {
    return PhysicsChunk(
        ChunkCollider(body, mutableListOf()),
        pos
    )
}

fun BodyFactory.emptyPhysicsChunk(pos: PhysicsChunkPos): PhysicsChunk {
    return EmptyPhysicsChunk(
        mutableCompoundBody(
            Shape.Multi(listOf()),
            ChunkTransform(pos)
        ),
        pos
    )
}