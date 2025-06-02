package org.qbrp.main.engine.assets.resourcepack

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import org.qbrp.main.core.assets.FileSystem.getOrCreate
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.log.LoggerUtil

@Autoload(LoadPriority.MODULE + 1)
class ResourcePackBakerModule: QbModule("resourcepack-baker"), ResourcePackAPI {
    companion object {
        val LOGGER = LoggerUtil.get("resources")
        val RESOURCEPACK_NODES = getOrCreate("qbrp/assets/nodes", true)
    }

    override fun getKoinModule() = inner<ResourcePackAPI>(this) {}

    override fun scanNodes(): List<Node> {
        return RESOURCEPACK_NODES
            .walkTopDown()
            .filter { it.isFile }
            .mapNotNull { file ->
                try {
                    val rawText = file.readText()
                    val parsedJsonObject = Json.parseToJsonElement(rawText).jsonObject
                    val idFromFilename = file.nameWithoutExtension
                    val withId: JsonObject = JsonObject(
                        parsedJsonObject + ("id" to JsonPrimitive(idFromFilename))
                    )
                    Json.decodeFromJsonElement<Node>(withId)
                } catch (e: Exception) {
                    LOGGER.error("Ошибка загрузки ${file.path}: ${e.message}")
                    null
                }
            }
            .toList()
    }

    override fun createModelPackFiles(resourcePack: ResourcePackBuilder, nodes: List<Node>) {
        nodes.forEach { node ->
            val jsonModel = node.createModel()
            resourcePack.addModel(jsonModel, modelName = node.modelId, relativePath = node.getPackContainerPath())
            node.getTextures().forEach {
                resourcePack.addTexture(it, textureName = Node.getAllowedName(it.nameWithoutExtension) , relativePath = node.getPackContainerPath())
            }
            if (node.type == "obj") resourcePack.addObjModel(
                text = node.getAdaptedObj(),
                mtl = node.getAdaptedMtl(),
                modelName = node.modelId,
                relativePath = node.getPackContainerPath())
        }
    }

    override fun createModelsList(nodes: List<Node>): ModelsList {
        val idsMap = mutableMapOf<String, String>()
        nodes.forEach { node ->
            idsMap[node.id] = node.getResourceLocation()
        }
        return ModelsList(idsMap)
    }
}