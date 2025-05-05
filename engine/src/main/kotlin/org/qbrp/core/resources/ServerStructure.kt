package org.qbrp.core.resources

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.qbrp.core.mc.Game
import org.qbrp.core.resources.content.DirectoryStorage
import org.qbrp.core.resources.data.StringData
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.core.resources.data.pack.ModelData
import org.qbrp.core.resources.parsing.ParserBuilder
import org.qbrp.core.resources.parsing.filters.ExtensionFilter
import org.qbrp.core.resources.structure.Branch
import org.qbrp.core.resources.structure.PackStructure
import org.qbrp.core.resources.structure.Structure
import java.io.File

class ServerStructure: Structure(File("qbrp")) {
    init {
        initFile()
    }
    var config = openConfig()

    val records = addBranch("records")
    val resources = Resources(this, config)

    val idGen = open("idgen", IdGenData::class.java).apply { autoInitialization = true }
    val youtubeToken = open("token.youtube-token", StringData::class.java)

    private fun openConfig() = (open(
        "config.yml",
        ServerConfigData::class.java,
        GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create(),
    ).data as ServerConfigData)
        .also { ConfigInitializationCallback.EVENT.invoker().onConfigUpdated(it) }
    fun reloadConfig() {
        config = openConfig()
    }

    class Resources(parentBranch: Branch, val config: ServerConfigData): Structure(parentBranch.resolve("resources") ) {

        val packStructure = addStructure(
            PackStructure(path = resolve("resourcepack"))) as PackStructure
        val itemResource = addBranch("item_resource")
        val blockResource = addBranch("block_reТsource")
        val packOverride = addBranch("overrides")

        val pack = ResourcePack(
            structure = packStructure,
            packMcMeta = config.resources.packMcMeta,
            icon = resolve("icon.png"),)

        fun bakeResourcePack() {
            Game.items.baseItems.forEach { pack.structure.addItemType(it.identifier, it.modelType) } // Для каждого предмета создаем свой файл модели
            ParserBuilder()
                .setClass(ModelData::class.java)
                .addFilter(ExtensionFilter(setOf("json")))
                .build()
                    .parse(itemResource.path.toFile())
                    .forEach { model -> pack.content.addModelBundle(
                        ModelData(ServerResources.parseJson(model.path.toFile()) as JsonObject), model.path
                        )
                    } // Парсим все модельки и делаем из них ModelBundle

            pack.structure.mod.pasteNonStructured(packOverride.path) // Добавляем overrides
            pack.bake(File(config.http.resourcePack))
        }

    }
}
