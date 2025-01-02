package org.qbrp.core.resources

import com.google.gson.JsonObject
import org.qbrp.core.game.Game
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.core.resources.data.pack.ItemConfigData
import org.qbrp.core.resources.data.pack.ModelData
import org.qbrp.system.utils.keys.Key
import org.qbrp.core.resources.parsing.ParserBuilder
import org.qbrp.core.resources.parsing.filters.ExtensionFilter
import org.qbrp.core.resources.structure.PackStructure
import org.qbrp.core.resources.structure.Structure
import java.io.File

class ServerStructure: Structure(File("qbrp")) {

    val resources = addStructure("resources")
    val items = addStructure("item")
    val blocks = addStructure("block")

    val packStructure = resources.addStructure(
        PackStructure(
            path = resources.path.resolve("resourcepack")
            .toFile())) as PackStructure
    val itemResource = resources.addBranch("item_resource")
    val blockResource = resources.addBranch("block_resource")
    val packOverride = resources.addBranch("overrides")

    val config = open("config.json", ServerConfigData::class.java).data as ServerConfigData
    val pack = ResourcePack(
        structure = packStructure,
        packMcMeta = config.resources.packMcMeta,
        icon = path.resolve("icon.png").toFile(),)

//    init {
//        items.parse(ParserBuilder()
//            .setClass(ItemConfigData::class.java)
//            .addFilter(ExtensionFilter(setOf("json")))
//            .setNaming { file -> Key("item_config_" + file.nameWithoutExtension) }
//            .build())
//    }

    fun printData() {
        logger.log("<<Предметы:>> ${items.contentRegistry.size}")
    }

    fun bakeResourcePack() {
        Game.items.baseItems.forEach { pack.structure.addItemType(it.identifier, it.modelType) }
        ParserBuilder()
            .setClass(ModelData::class.java)
            .addFilter(ExtensionFilter(setOf("json")))
            .setNaming { file -> Key(file.path) }
            .build()
        .parse(itemResource.path.toFile())
        .forEach { model -> pack.content.addModelBundle(
            ModelData(ServerResources.parseJson(model.path.toFile()) as JsonObject), model.path
            )
        }
        pack.structure.mod.pasteNonStructured(packOverride.path)
        pack.bake(File(config.http.resourcePack)) { pack.structure.save() }
    }
}
