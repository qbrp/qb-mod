package org.qbrp.main.engine.physics.dynamics.chunk.factory

import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter

fun ChunkCollider.particleBlocks(world: ServerWorld, chunkPos: ChunkPos, count: Int = 10) {
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