package org.qbrp.engine.client.core.visual

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.qbrp.core.visual.VisualDataNetworking.Companion.CLIENT_MAX_LOAD_RADIUS
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.GET_CHUNK_VISUAL
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.utils.log.Loggers
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.core.visual.data.VisualData
import org.qbrp.core.visual.data.VisualPlayerData
import org.qbrp.system.networking.messages.components.Cluster

object VisualDataLoader {
    val logger = Loggers.get("visualData", "loader")

    // Перегрузка с доп. проверкой, чтобы не загружать VisualStorage данными, которые игрок точно не увидит
    fun chunkRequest(chunkPos: ChunkPos, sourcePos: Vec3d) {
        val dx = chunkPos.centerX - sourcePos.x
        val dz = chunkPos.centerZ - sourcePos.z
        if (dx * dx + dz * dz > CLIENT_MAX_LOAD_RADIUS * CLIENT_MAX_LOAD_RADIUS) {
            return
        }

        chunkRequest(chunkPos)
    }

    fun chunkRequest(chunkPos: ChunkPos) {
        ClientNetworkManager.sendMessage(Message(
            GET_CHUNK_VISUAL,
            StringContent().apply { string = "${chunkPos.centerX},${chunkPos.centerZ}" }
        ))
    }

    fun loadContent(cluster: Cluster) {
        val world: World = MinecraftClient.getInstance().world ?: return // Проверяем на null
        try {
            VisualDataStorage.loadContent(convertVisual(cluster, world)!!)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun loadChunk(clusters: List<Cluster>) {
        val world: World = MinecraftClient.getInstance().world ?: return // Проверяем на null

        VisualDataStorage.loadChunk(clusters.map { element ->
            convertVisual(element, world)!!
        })
    }

    fun convertVisual(visualCluster: Cluster, world: World): VisualData? {
        val cluster = visualCluster.getData()
        val type = cluster.getHeader<String>()
        if (type == "player") {
            val nickname = cluster.getComponentData<String>("nickname")
            val player = world.players.firstOrNull { it.name.string == nickname }
            if (player != null) {
                return VisualPlayerData(player, cluster.getComponentData<String>("uuid")!!).apply {
                    isWriting = cluster.getComponentData<Boolean>("isWriting")!!
                }
            }
        }
        return null
    }



}