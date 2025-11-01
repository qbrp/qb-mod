package org.qbrp.main.engine.physics.chunk

import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import java.util.concurrent.ConcurrentHashMap

class ChunkBank(
    private val worldBodyManager: WorldBodyManager,
    private val bodyFactory: BodyFactory
) : PhysicsChunkBank, PhysicsChunkManager {
    private val chunkMap: ConcurrentHashMap<PhysicsChunkPos, PhysicsChunk> = ConcurrentHashMap()
    override val chunks: List<PhysicsChunk> get() = chunkMap.values.toList()

    override fun getChunkAt(pos: PhysicsChunkPos): PhysicsChunk? {
        return chunkMap[pos]
    }

    override fun createChunk(pos: PhysicsChunkPos): PhysicsChunk {
        removeChunk(pos)
        val chunk = bodyFactory.emptyPhysicsChunk(pos)
        chunkMap[pos] = chunk
        worldBodyManager.addChunk(chunk)
        return chunk
    }

    override fun appendChunk(pos: PhysicsChunkPos, startY: Int, endY: Int, chunkSegments: ChunkSegments) {
        val chunk = chunkMap[pos] ?: createChunk(pos)
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

    override fun removeChunkPart(pos: PhysicsChunkPos, index: Int) {
        val chunk = chunkMap[pos] ?: return
        val chunkCollider = chunk.collider
        chunkCollider.parts.removeAt(index)
        chunkCollider.body.removeShape(index)
        if (chunkCollider.parts.isEmpty()) {
            removeChunk(pos)
        }
    }

    override fun removeChunk(pos: PhysicsChunkPos): PhysicsChunk? {
        return chunkMap.remove(pos)?.also {
            worldBodyManager.removeChunk(it)
        }
    }
}