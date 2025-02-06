package org.qbrp.core.resources

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.qbrp.system.utils.log.Logger
import org.qbrp.system.utils.log.Loggers
import java.io.File

object ServerResources {
    fun getLogger(): Logger = Loggers.get("resources")

    private lateinit var root: ServerStructure

    fun getRootBranch() = root
    fun getConfig() = root.config

    fun buildResources() {
        root = ServerStructure()
        root.resources.bakeResourcePack()
        root.printData()
    }

    fun parseJson(file: File): JsonObject? =
        validateFile(file)?.let {
            runCatching {
                JsonParser.parseString(it.readText()).asJsonObject
            }.getOrElse { ex ->
                getLogger().error("Ошибка при парсинге файла ${file.absolutePath}: ${ex.message}")
                null
            }
        }

    private fun validateFile(file: File): File? =
        file.takeIf { it.exists() && it.isFile } ?: run {
            getLogger().error("Файл не найден или не является файлом: ${file.absolutePath}")
            null
        }
}