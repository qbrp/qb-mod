package org.qbrp.client.engine.physics

import net.minecraft.util.math.ChunkPos
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.chunk.ChunkBank
import org.qbrp.main.engine.physics.chunk.ChunkCollider
import org.qbrp.main.engine.physics.chunk.ChunkDescriptor
import org.qbrp.main.engine.physics.chunk.PhysicsChunk
import org.qbrp.main.network.physics.ChunkData
import kotlin.math.abs

fun ChunkBank.loadChunkFromPacketData(chunk: ChunkData, factory: BodyFactory) {
    val colliderShape = chunk.colliderShape
    val pos = chunk.pos
    val body = colliderShape?.let {
        val descriptor = ChunkDescriptor(pos, colliderShape)
        factory.build(descriptor)
    }
    setChunk(PhysicsChunk(chunk.minY, chunk.maxY, ChunkCollider(body), pos))
}

fun ChunkBank.cleanFarChunks(position: ChunkPos, distance: Int) {
    for (chunk in chunks) {
        if (abs(chunk.pos.x - position.x) > distance || abs(chunk.pos.z - position.z) > distance) {
            removeChunk(chunk.pos)
        }
    }
}
