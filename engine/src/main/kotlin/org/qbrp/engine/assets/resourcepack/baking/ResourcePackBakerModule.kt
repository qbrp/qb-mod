package org.qbrp.engine.assets.resourcepack.baking

import kotlinx.serialization.json.Json
import org.qbrp.core.assets.FileSystem.getOrCreate
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.modules.QbModule
import org.qbrp.system.utils.log.Loggers
import java.io.File

@Autoload
class ResourcePackBakerModule: QbModule("resourcepack-baker"), ResourcePackAPI {
    companion object {
        val LOGGER = Loggers.get("resources")
        val RESOURCEPACK_NODES = getOrCreate("qbrp/assets/nodes", true)
    }

    override fun getAPI(): ResourcePackAPI = this

    override fun bakeResourcePack(path: File) {
        path.deleteRecursively()
        val pack = ResourcePack(path)
        val nodes = scanNodes()
        createModelPackFiles(pack, nodes)
    }

    fun scanNodes(): List<Node> {
        return RESOURCEPACK_NODES
            .walkTopDown()
            .filter { it.isFile }
            .mapNotNull { file ->
                try {
                    Json.decodeFromString<Node>(file.readText())
                } catch (e: Exception) {
                    LOGGER.error("Ошибка загрузки ${file.path}: ${e.message}")
                    null
                }
            }
            .toList()
    }

    fun createModelPackFiles(resourcePack: ResourcePack, nodes: List<Node>) {
        nodes.forEach { node ->
            val jsonModel = node.createJsonModel()
            resourcePack.addModel(jsonModel, modelName = node.id, relativePath = node.getPackContainerPath())
            resourcePack.addObjModel(
                text = node.getAdaptedObj(),
                mtl = node.getAdaptedMtl(),
                modelName = node.id,
                relativePath = node.getPackContainerPath())
            node.getTextures().forEach {
                resourcePack.addTexture(it, textureName = Node.getAllowedName(it.nameWithoutExtension) , relativePath = node.getPackContainerPath())
            }
        }
    }
}