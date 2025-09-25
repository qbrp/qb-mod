package org.qbrp.main.engine.physics.dynamics.chunk.factory

import net.minecraft.server.world.ServerWorld
import net.minecraft.world.chunk.Chunk

interface ChunkColliderFactory {
    fun build(world: ServerWorld, chunk: Chunk, settings: LoadSettings): ChunkCollider
}