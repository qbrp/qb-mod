package org.qbrp.main.engine.physics.chunk

import net.minecraft.util.math.BlockPos
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import java.util.concurrent.ConcurrentHashMap

class PhysicsChunkManager(
    private val physicsChunkBank: PhysicsChunkBank,
    private val worldBodyManager: WorldBodyManager,
    private val chunkLoadModifySystem: ChunkLoadModifySystem
) {
    fun createChunk(pos: PhysicsChunkPos) {
        chunkLoadModifySystem.addChunk(pos)
    }

    fun appendChunk(pos: PhysicsChunkPos, startY: Int, endY: Int, chunkSegments: ChunkSegments) {
        val chunk = physicsChunkBank.getChunkAt(pos) ?: createChunk(pos)
        var minY = startY
        var maxY = endY
        val chunkCollider = chunk.collider
        for (chunkPart in chunkCollider.parts) {
            val chunkMinY = chunkPart.startY
            val chunkMaxY = chunkPart.endY
            if (chunkMinY <= minY) {
                minY = chunkMinY - 1
            }
            if (chunkMaxY >= maxY) {
                maxY = chunkMaxY + 1
            }
        }
        // Убираем уже использованные сегменты в чанке, загружаем неиспользованные
        val segments = chunkSegments.shape.subList(minY, maxY)
        chunkCollider.body.addShape(segments, ChunkTransform(pos))
        chunkCollider.parts += ChunkPart(startY, endY, ChunkSegments(segments))
    }

    fun addFullSizeBlock(pos: BlockPos) {
        val pos = pos.getPhysicsChunkPos()
        val chunk = physicsChunkBank.getChunkAt(pos) ?: return
    }

    fun addBlock(pos: BlockPos, blockShape: Shape) {

    }

    fun removeBlockAndRebuildPart(pos: BlockPos) {

    }

    fun removeChunkPart(pos: PhysicsChunkPos, index: Int) {
        val chunk = physicsChunkBank.getChunkAt(pos) ?: return
        val chunkCollider = chunk.collider
        chunkCollider.parts.removeAt(index)
        chunkCollider.body.removeShape(index)
        if (chunkCollider.parts.isEmpty()) {
            removeChunk(pos)
        }
    }

    fun removeChunk(pos: PhysicsChunkPos): PhysicsChunk? {
        return physicsChunkBank.removeChunk(pos)?.also {
            worldBodyManager.removeChunk(it)
        }
    }
}і

interface PhysicsChunkAccess {
    val chunks: List<PhysicsChunk>
    fun getChunkAt(pos: PhysicsChunkPos): PhysicsChunk?
    fun isChunkLoadedAt(pos: PhysicsChunkPos): Boolean
}

class PhysicsChunkBank : PhysicsChunkAccess {
    private val chunkMap: ConcurrentHashMap<PhysicsChunkPos, PhysicsChunk> = ConcurrentHashMap()
    override val chunks: List<PhysicsChunk> get() = chunkMap.values.toList()

    override fun getChunkAt(pos: PhysicsChunkPos): PhysicsChunk? {
        return chunkMap[pos]
    }

    override fun isChunkLoadedAt(pos: PhysicsChunkPos): Boolean {
        return getChunkAt(pos) != null
    }

    fun setChunk(pos: PhysicsChunkPos, chunk: PhysicsChunk) {
        chunkMap[pos] = chunk
    }

    fun removeChunk(pos: PhysicsChunkPos): PhysicsChunk? {
        return chunkMap.remove(pos)
    }
}