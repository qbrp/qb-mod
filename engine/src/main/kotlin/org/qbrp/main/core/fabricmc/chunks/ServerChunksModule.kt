package org.qbrp.main.core.fabricmc.chunks

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import org.koin.core.component.get
import org.koin.core.module.Module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import java.util.concurrent.ConcurrentHashMap

@Autoload
class ServerChunksModule() : EngineModule("chunk-loader"), ChunkStorage {
    private val loadedChunks: ConcurrentHashMap<ChunkPos, Chunk> = ConcurrentHashMap()
    private val dirtyChunks: MutableSet<ChunkPos> = mutableSetOf()
    override val chunks: Collection<Chunk> get() = loadedChunks.values.mapNotNull { getChunk(it.pos) }
    override val world: ServerWorld by lazy { get<MinecraftServer>().overworld }

    override fun getKoinModule(): Module = onlyApi<ChunkStorage>(this)

    override fun onEnable() {
        ServerChunkEvents.CHUNK_LOAD.register { _, chunk ->
            loadedChunks[chunk.pos] = chunk
        }
        ServerChunkEvents.CHUNK_UNLOAD.register { _, chunk ->
            loadedChunks.remove(chunk.pos)
        }
        PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, entity ->
            if (world != this.world) return@register
            dirtyChunks.add(ChunkPos(pos))
            MainWorldBlockEvents.BREAK.invoker().onBreak(pos)
        }
        UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
            if (world != this.world) return@register ActionResult.PASS
            if (hitResult.isInsideBlock) {
                dirtyChunks.add(ChunkPos(hitResult.blockPos))
                MainWorldBlockEvents.ADDED.invoker().onAdded(hitResult.blockPos)
            }
            ActionResult.PASS
        }
    }

    override fun getChunk(pos: ChunkPos): Chunk? {
        return if (pos in dirtyChunks) {
            dirtyChunks -= pos
            world.chunkManager.getWorldChunk(pos.x, pos.z)
        } else {
            loadedChunks[pos]
        }
    }

    override fun getChunk(pos: BlockPos): Chunk? {
        val chunkX = pos.x shr 4
        val chunkZ = pos.z shr 4
        val chunkPos = ChunkPos(chunkX, chunkZ)
        return getChunk(chunkPos)
    }
}