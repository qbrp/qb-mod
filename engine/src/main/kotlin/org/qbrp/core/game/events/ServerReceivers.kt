package org.qbrp.core.game.events

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import org.bson.json.JsonObject
import org.qbrp.system.networking.messages.JsonArrayContent
import org.qbrp.system.networking.messages.Messages.GET_CHUNK_VISUAL
import org.qbrp.system.networking.messages.Messages.LOAD_CHUNK_VISUAL
import org.qbrp.system.networking.messages.ServerReceiver
import org.qbrp.system.networking.messages.StringContent
import org.qbrp.visual.VisualDataStorage
import org.qbrp.visual.VisualDataStorage.toJsonArray

object ServerReceivers {
    fun register() {
        ServerReceiver(GET_CHUNK_VISUAL, StringContent::class) { message, context, receiver ->
            val content = (message.content as StringContent).string
            val x = content.split(",")[0].toInt()
            val z = content.split(",")[1].toInt()
            val objects = VisualDataStorage.getObjectsInChunk(ChunkPos(BlockPos(x, 0 , z)))
            if (objects.isNotEmpty()) {
                receiver.response(
                    JsonArrayContent().apply { array = objects.toJsonArray() },
                    context,
                    LOAD_CHUNK_VISUAL
                )
            }
        }.register()
    }

}