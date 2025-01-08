package org.qbrp.engine.client.visual

import com.google.gson.Gson
import com.google.gson.JsonArray
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.GET_CHUNK_VISUAL
import org.qbrp.system.networking.messages.StringContent
import org.qbrp.visual.VisualData
import org.qbrp.system.utils.log.Loggers
import org.qbrp.visual.VisualDataStorage

object VisualDataLoader {
    val gson = Gson()
    val logger = Loggers.get("visualData", "loader")

    fun chunkRequest(chunkPos: ChunkPos) {
        ClientNetworkManager.sendMessage(Message(
            GET_CHUNK_VISUAL,
            StringContent().apply { string = "${chunkPos.centerX},${chunkPos.centerZ}" }
        ))
    }

    fun loadChunk(json: JsonArray) {
        val world: World = MinecraftClient.getInstance().world as World
        VisualDataStorage.loadChunk(json.map { jsonElement ->
            val jsonObject = jsonElement.asJsonObject // Преобразуем в JsonObject
            val clazzName = jsonObject.get("clazz").asString // Получаем имя класса
            val clazz = Class.forName(clazzName).kotlin // Получаем Class объекта
            val dataInstance = gson.fromJson(jsonObject.toString(), clazz.java) as VisualData
            dataInstance.fromJson(jsonObject.toString(), world) // Настраиваем объект
            dataInstance
            }
        )
    }

}