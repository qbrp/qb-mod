package org.qbrp.main.engine.assets.resourcepack

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.qbrp.main.core.assets.FileSystem.getOrCreate
import org.qbrp.main.engine.assets.resourcepack.models.JsonModel
import org.qbrp.main.engine.assets.resourcepack.models.Model
import org.qbrp.main.engine.assets.resourcepack.models.ObjModel
import java.io.File

class ResourcePackBuilder(val path: File) {
    val qbrp = getOrCreate(path.resolve("assets/qbrp"), true)
    val models = getOrCreate(qbrp.resolve("models"), true)
    val textures = getOrCreate(qbrp.resolve("textures"), true)
    private val packMcMetaData = PackMcMeta(PackMcMeta.Pack("qbrp Content Pack", 56))
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
        val fileName = "$modelName.json"
        val modelPath = models.resolve(relativePath).resolve(fileName)
        getOrCreate(modelPath)

        val jsonString = when (model) {
            is ObjModel -> Json.encodeToString(ObjModel.serializer(), model)
            is JsonModel -> Json.encodeToString(JsonModel.serializer(), model)
            else -> throw IllegalArgumentException()
        }

        modelPath.writeText(jsonString)
        ResourcePackBakerModule.LOGGER.log("<<[+ Model]>> $modelPath")
    }
}