package org.qbrp.main.engine.physics.chunk

import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.service.BodyFactory

fun ChunkBank.loadServerChunk(
    chunk: Chunk,
    world: ServerWorld,
    settings: ChunkLoadSettings,
    bodyFactory: BodyFactory,
    particle: Boolean
) {
    unloadServerChunk(chunk)
    val collider = buildChunkCompoundShape(world, chunk, settings)
    if (particle) collider.particleShapes(world, chunk.pos)
    val chunkBody = bodyFactory.chunkCompoundBody(chunk.pos.asPhysics(), collider)

    val physicsChunkPos = chunk.pos.asPhysics()
    val physicsChunk = PhysicsChunk(
        settings.minHeight,
        settings.maxHeight,
        ChunkCollider(chunkBody, mutableListOf()),
        physicsChunkPos

    )
    setChunk(physicsChunk)
}

fun ChunkBank.unloadServerChunk(chunk: Chunk) {
    removeChunk(chunk.pos.asPhysics())
}

fun Shape.SegmentCompound.particleShapes(world: ServerWorld, chunkPos: ChunkPos, count: Int = 2) =
    world.server.execute {
        segments.forEach {
            val flats = it.flats
            val y = it.y
            for (flat in flats) {
                for (x in flat.startX..flat.endX) {
                    for (z in flat.startZ..flat.endZ) {
                        val x1 = chunkPos.startX + x
                        val z1 = chunkPos.startZ + z
                        world.spawnParticles(ParticleTypes.FLAME,
                            x1.toDouble() + 0.5, y.toDouble() + 0.5, z1.toDouble() + 0.5, count, 0.5, 0.5, 0.5, 0.005)
                    }
                }
            }
        }
    }