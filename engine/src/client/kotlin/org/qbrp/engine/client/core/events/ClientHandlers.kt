package org.qbrp.engine.client.core.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.minecraft.client.world.ClientWorld
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import org.qbrp.engine.client.visual.VisualDataLoader

object ClientHandlers {

    fun registerEvents() {
        ClientEntityEvents.ENTITY_LOAD.register { entity, world ->
            if (entity is ServerPlayerEntity) {
                val chunkPos = ChunkPos(BlockPos(entity.pos.x.toInt(), 0, entity.pos.z.toInt()))
                VisualDataLoader.chunkRequest(chunkPos)
            }
        }
        ClientChunkEvents.CHUNK_LOAD.register { world: ClientWorld, chunk: Chunk ->
            val chunkPos = chunk.getPos()
            VisualDataLoader.chunkRequest(chunkPos)
        }
        ClientChunkEvents.CHUNK_UNLOAD.register { world: ClientWorld, chunk: Chunk ->

        }
    }

}