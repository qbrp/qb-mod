package org.qbrp.core.resources

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.resources.commands.ResourcesCommands
import org.qbrp.core.resources.data.config.KreisyEmojiConfig
import org.qbrp.core.resources.structure.Structure
import org.qbrp.system.utils.log.Logger
import org.qbrp.system.utils.log.Loggers
import java.io.File

object ServerResources {
    fun getLogger(): Logger = Loggers.get("resources")
    init { CommandsRepository.add(ResourcesCommands()) }

    private lateinit var root: ServerStructure
    private val plugins = Structure(File("plugins"))
        .apply { initFile() }

    fun getRootBranch() = root
    fun getConfig() = root.config
    fun reloadConfig() = root.reloadConfig()

    fun getItems() = root.items

    fun buildResources() {
        root = ServerStructure()
//        root.resources.bakeResourcePack()
//        root.printData()
    }

    fun getKreisyEmojiConfig(): KreisyEmojiConfig {
        plugins.apply {
            val plugin = addBranch("ChatEmojiPlugin")
            return plugin.open("config.yml", KreisyEmojiConfig::class.java).data as KreisyEmojiConfig
        }
    }

    fun buildContent() {
        root.items.openDirectories()
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