package org.qbrp.visual

import com.google.gson.JsonArray
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.ChunkPos
import org.qbrp.system.utils.log.Loggers
import org.spongepowered.include.com.google.gson.Gson

object VisualDataStorage {
    private val gson = Gson()
    val logger = Loggers.get("visualData", "storage")
    private val visualData = mutableListOf<VisualData>()

    fun loadPlayer(player: ServerPlayerEntity) = loadContent(VisualPlayerData(player))
    fun unloadPlayer(player: ServerPlayerEntity) {
        visualData
            .filterIsInstance<VisualPlayerData>()
            .filter { it.player.name == player.name }
            .forEach { unloadContent(it) }
    }

    fun loadChunk(dataList: List<VisualData>) {
        visualData.addAll(dataList)
        logger.log("<<[+ CHUNK]>> $dataList (${visualData.size})")
    }

    private fun loadContent(content: VisualData) {
        visualData.add(content)
        logger.log("<<[+]>> $content")
    }

    private fun unloadContent(content: VisualData) {
        visualData.add(content)
        logger.log("<<[-]>> $content")
    }

    fun getObjectsInChunk(chunkPos: ChunkPos): List<VisualData> {
        //println("${chunkPos.startX}, ${chunkPos.startZ}, ${chunkPos.endX}, ${chunkPos.endZ} | $visualData")
        return visualData.filter {
            it.x in chunkPos.startX..chunkPos.endX &&
            it.z in chunkPos.startZ..chunkPos.endZ
        }
    }

    fun List<VisualData>.toJsonArray(): JsonArray {
        val jsonArray = JsonArray()
        this.forEach { jsonArray.add(it.toJson()) } // Добавляем каждый элемент как JsonElement
        return jsonArray
    }
}