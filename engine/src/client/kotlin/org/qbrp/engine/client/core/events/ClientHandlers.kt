package org.qbrp.engine.client.core.events

import icyllis.modernui.mc.MuiModApi
import icyllis.modernui.mc.UIManager
import icyllis.modernui.mc.MuiScreen
import icyllis.modernui.mc.fabric.MuiFabricApi
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import org.qbrp.engine.client.core.visual.VisualDataLoader
import org.qbrp.engine.client.render.hud.chat.ChatInputScreen

object ClientHandlers {

    val client = MinecraftClient.getInstance()
    val player = client.player

    fun registerEvents() {
        ClientEntityEvents.ENTITY_LOAD.register { entity, world ->
            if (entity is ServerPlayerEntity) {
                val chunkPos = ChunkPos(BlockPos(entity.pos.x.toInt(), 0, entity.pos.z.toInt()))
                player?.pos?.let {
                    VisualDataLoader.chunkRequest(chunkPos)
                }
            }
        }
    }


    fun registerChunkLoadEvents() {
        ClientChunkEvents.CHUNK_LOAD.register { world: ClientWorld, chunk: Chunk ->
            val chunkPos = chunk.getPos()
            VisualDataLoader.chunkRequest(chunkPos)
        }
        ClientChunkEvents.CHUNK_UNLOAD.register { world: ClientWorld, chunk: Chunk ->

        }

    }
}