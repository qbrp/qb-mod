package org.qbrp.main.deprecated.resources

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.koin.core.component.KoinComponent
import org.qbrp.deprecated.resources.commands.ResourcesCommands
import org.qbrp.deprecated.resources.data.config.KreisyEmojiConfig
import org.qbrp.deprecated.resources.structure.Structure
import org.qbrp.main.core.utils.log.Logger
import org.qbrp.main.core.utils.log.LoggerUtil
import java.io.File
import org.koin.core.component.get
import org.qbrp.deprecated.resources.ServerStructure
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.engine.ModInitializedEvent

@Deprecated("Использовать ассеты, FileSystem или koin-инъекцию")
object ServerResources: KoinComponent {
    fun getLogger(): Logger = LoggerUtil.get("resources")
    init {
        ModInitializedEvent.EVENT.register {
            get<CommandsAPI>().add(ResourcesCommands())
        }
    }

    private lateinit var root: ServerStructure
    private val plugins = Structure(File("plugins"))
        .apply { initFile() }

    fun getRootBranch() = root
    fun getConfig() = root.config
    fun reloadConfig() = root.reloadConfig()

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