package org.qbrp.main.core.fabricmc.chunks

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import java.util.Spliterator

interface ChunkStorage: Iterable<Chunk> {
    val chunks: Collection<Chunk>
    val world: ServerWorld
    fun getChunk(pos: ChunkPos): Chunk?
    fun getChunk(pos: BlockPos): Chunk?
    override fun iterator(): Iterator<Chunk> = chunks.iterator()
    override fun spliterator(): Spliterator<Chunk> = chunks.spliterator()
}