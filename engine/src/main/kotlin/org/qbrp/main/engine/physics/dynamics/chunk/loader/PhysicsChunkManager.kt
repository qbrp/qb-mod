package org.qbrp.main.engine.physics.dynamics.chunk.loader

import net.minecraft.server.world.ServerWorld
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.engine.physics.dynamics.chunk.factory.LoadSettings

interface PhysicsChunkManager {
    fun load(chunk: Chunk, world: ServerWorld, settings: LoadSettings)
    fun unload(chunk: Chunk, world: ServerWorld)
}