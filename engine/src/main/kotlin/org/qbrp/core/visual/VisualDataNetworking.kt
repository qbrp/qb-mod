package org.qbrp.core.visual

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import org.qbrp.core.visual.VisualDataStorage.toCluster
import org.qbrp.core.visual.data.VisualData
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.LOAD_CHUNK_VISUAL
import org.qbrp.system.networking.messages.Messages.UPDATE_VISUAL
import org.qbrp.system.networking.messages.types.ClusterListContent
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.networking.messaging.NetworkManager
import org.qbrp.system.networking.messaging.ServerReceiver
import org.qbrp.system.networking.messaging.ServerReceiverContext

class VisualDataNetworking() {

    companion object {
        const val CLIENT_MAX_LOAD_RADIUS = 64
    }

    fun handleGetRequest(content: StringContent, receiver: ServerReceiver<ServerReceiverContext>, context: ServerReceiverContext): Boolean {
        val content = content.getData()
        val x = content.split(",")[0].toInt()
        val z = content.split(",")[1].toInt()
        val objects = VisualDataStorage.getObjectsInChunk(ChunkPos(BlockPos(x, 0 , z)))
        if (objects.isNotEmpty()) {
            receiver.response(
                ClusterListContent().apply { list = objects.toCluster() },
                context,
                LOAD_CHUNK_VISUAL
            )
        }
        return true
    }

    fun sendHardSingleUpdateMessage(updatedObject: VisualData) {
        NetworkManager.broadcastArea(
            updatedObject.world as ServerWorld,
            updatedObject.x.toDouble(),
            updatedObject.z.toDouble(),
            CLIENT_MAX_LOAD_RADIUS.toDouble(),
            Message(
                UPDATE_VISUAL,
                updatedObject.toCluster()
                )
        )
    }

}