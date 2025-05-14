package org.qbrp.engine.assets.resourcepack.baking

import kotlinx.serialization.json.Json
import org.qbrp.core.assets.FileSystem.getOrCreate
import java.io.File

class ResourcePack(val path: File) {
    val qbrp = getOrCreate(path.resolve("assets/qbrp"), true)
    val models = getOrCreate(qbrp.resolve("models"), true)
    val textures = getOrCreate(qbrp.resolve("textures"), true)
    private val packMcMetaData = PackMcMeta(PackMcMeta.Pack("qbrp Content Pack"))
    val packMcMeta = getOrCreate(path.resolve("pack.mcmeta")).writeText(Json.encodeToString(packMcMetaData))

    fun addTexture(file: File, textureName: String = file.nameWithoutExtension, relativePath: String = "") {
        if (file.extension != "png" && file.extension != "jpg") {
            throw FileSystemException(file, reason = "Файл не имеет расширения png или jpg")
        }
        val textureName = "$textureName.png"
        val texturePath = textures.resolve(relativePath).resolve(textureName)

        file.copyTo(texturePath)
        ResourcePackBakerModule.Companion.LOGGER.log("<<[+ Texture]>> $texturePath")
    }

    fun addObjModel(file: File, mtl: String, modelName: String = file.nameWithoutExtension, relativePath: String = "") {
        addModel(file, modelName, "obj", relativePath)
        val mtlPath = models.resolve(relativePath).resolve("$modelName.mtl")
        getOrCreate(mtlPath).writeText(mtl)
    }

    fun addObjModel(text: String, mtl: String, modelName: String, relativePath: String = "") {
        addModel(text, modelName, "obj", relativePath)
        val mtlPath = models.resolve(relativePath).resolve("$modelName.mtl")
        getOrCreate(mtlPath).writeText(mtl)
    }

    fun addModel(file: File, modelName: String = file.nameWithoutExtension, extension: String = "json", relativePath: String = "") {
        if (file.extension != "json" && file.extension != "obj") {
            throw FileSystemException(file, reason = "Файл не имеет расширение json или obj")
        }
        val modelName = "$modelName.$extension"
        val modelPath = models.resolve(relativePath).resolve(modelName)

        file.copyTo(modelPath)
        ResourcePackBakerModule.Companion.LOGGER.log("<<[+ Model]>> $modelPath")
    }

    fun addModel(text: String, modelName: String, extension: String = "json", relativePath: String = "") {
        val modelName = "$modelName.$extension"
        val modelPath = models.resolve(relativePath).resolve(modelName)

        modelPath.writeText(text)
        ResourcePackBakerModule.Companion.LOGGER.log("<<[+ Model]>> $modelPath")
    }

    fun addModel(model: Model, modelName: String = model.getName(), relativePath: String = "") {
        val modelName = "$modelName.json"
        val modelPath = models.resolve(relativePath).resolve(modelName)

        getOrCreate(modelPath)
        modelPath.writeText(Json.encodeToString(model))
        ResourcePackBakerModule.Companion.LOGGER.log("<<[+ Model]>> $modelPath")
    }
}