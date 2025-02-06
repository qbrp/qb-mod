package org.qbrp.core.visual

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import org.qbrp.core.visual.data.VisualData
import org.qbrp.core.visual.data.VisualPlayerData
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.utils.log.Loggers

object VisualDataStorage {
    val logger = Loggers.get("visualData", "storage")
    val visualDataNetworking = VisualDataNetworking()
    private val visualData = mutableMapOf<String, VisualData>()

    const val SERVER_MAX_LOAD_RADIUS = 64

    fun loadPlayer(player: ServerPlayerEntity) = loadContent(VisualPlayerData(player))
    fun unloadPlayer(player: ServerPlayerEntity) {
        visualData.values
            .filterIsInstance<VisualPlayerData>()
            .filter { it.player.name == player.name }
            .forEach { unloadContent(it) }
    }

    fun loadChunk(dataList: List<VisualData>) {
        dataList.forEach { loadContent(it) }
        logger.log("<<[+ CHUNK]>> $dataList (${visualData.size})")
    }

     fun loadContent(content: VisualData) {
        visualData[content.uuid] = content
        logger.log("<<[+]>> $content")
    }

    private fun unloadContent(content: VisualData) {
        visualData.remove(content.uuid)
        logger.log("<<[-]>> $content")
    }

    fun getPlayer(name: String): VisualPlayerData? {
        return visualData.values
            .filterIsInstance<VisualPlayerData>()
            .find { it.player.name.string == name }
    }

    fun <T> getContent(key: String): T? {
        return visualData.getOrElse(key) { null } as T
    }

    fun getObjectsInChunk(chunkPos: ChunkPos, sourcePos: BlockPos): List<VisualData> {
        val dx = chunkPos.centerX - sourcePos.x
        val dz = chunkPos.centerZ - sourcePos.z
        if (dx * dx + dz * dz > SERVER_MAX_LOAD_RADIUS * SERVER_MAX_LOAD_RADIUS) {
            return emptyList()
        }

        return getObjectsInChunk(chunkPos)
    }

    fun getObjectsInChunk(chunkPos: ChunkPos): List<VisualData> {
        //println("${chunkPos.startX}, ${chunkPos.startZ}, ${chunkPos.endX}, ${chunkPos.endZ} | $visualData")
        return visualData.values.filter {
            it.x in chunkPos.startX..chunkPos.endX &&
            it.z in chunkPos.startZ..chunkPos.endZ
        }
    }

    fun List<VisualData>.toCluster(): List<Cluster> {
        return map {
            it.toCluster()
        }
    }

}