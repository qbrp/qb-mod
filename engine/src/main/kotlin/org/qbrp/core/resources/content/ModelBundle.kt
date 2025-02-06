package org.qbrp.core.resources.content

import org.qbrp.core.resources.data.pack.ModelData
import org.qbrp.core.resources.data.pack.PredicatesData
import org.qbrp.core.resources.structure.PackStructure
import org.qbrp.core.resources.PackContent
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.units.ContentUnit
import org.qbrp.system.utils.keys.Key
import org.qbrp.system.utils.format.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.pathString

class ModelBundle(
    model: ModelData,
    directory: Path,
    val packStructure: PackStructure
) {
    val customModelData = PackContent.genCustomModelData()

    val textures = loadTextures(model, directory.parent.toString())
    val modelUnit = packStructure.addModel(model).apply {
        (data as ModelData).processTextures(textures)
        save()
    }

    init {
        packStructure.itemTypes.children.forEach { itemType ->
            val file = itemType as ContentUnit
            val data = file.data as PredicatesData
            data.addPredicate(
                modelUnit.pathString().getRelative("models").pathToJsonFormat().removeExtensions(), customModelData
            )
        }
    }

    fun loadTextures(model: ModelData, modelPath: String): List<String> {
        return model.textures.values.map { texture ->
            val textureUnit = packStructure.addTexture(
                Paths.get(
                    ServerResources.getRootBranch().resources.itemResource.pathString(),
                    texture.replace(":", "/")
                        .replace("./", "$modelPath/")
                        .replace("resources", "") + ".png"
                )
            )
            "qbrp:" + textureUnit.path.toString().getRelative("textures").pathToJsonFormat().removeExtensions()
        }
    }
}
