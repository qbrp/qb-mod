package org.qbrp.engine.assets.resourcepack.baking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.qbrp.core.assets.FileSystem
import java.io.File

@Serializable
data class Node(val type: String,
                val model: String,
                @SerialName("for") val resourceType: String,
                @SerialName("material_textures")  val materialTextures: Map<String, String>,
                val display: DisplayConfig? = null,
                @SerialName("flip_v") val flipV: Boolean = false,
                @SerialName("test_item") val createTestItem: Boolean = false,
                val id: String = getAllowedName(model.split("/").last())) {

    companion object {
        val OBJ_LOADER_PARENT = "special-model-loader:builtin/obj"

        fun getAllowedName(name: String): String {
            return name.split('/').last()
                .lowercase()
                .replace(" ", "-")
        }
        fun getPackPath(relative: String, root: String = "qbrp") = "$root:$relative"
    }

    fun getAdaptedMtl(): String {
        val mtlFile = FileSystem.ASSETS.resolve("$model.mtl")

        val mtlText = mtlFile.readLines()
        val modifiedLines = mutableListOf<String>()
        for (line in mtlText) {
            modifiedLines.add(line)
            if (line.contains("newmtl")) {
                val materialName = line.split("newmtl ").last()
                materialTextures[materialName]?.let {
                    // Добавляем строку после current line
                    val textureName = getAllowedName(it.split("/").last())
                    modifiedLines.add("map_Kd ${getPackPath(getPackContainerPath() + "/$textureName")}") // Пример: добавляем путь к текстуре
                }
            }
        }

        return modifiedLines.joinToString("\n")
    }

    fun getAdaptedObj(): String {
        val objFile = FileSystem.ASSETS.resolve("$model.obj")
        return objFile.readLines().joinToString("\n") { line ->
            if (line.startsWith("mtllib ")) {
                "mtllib ${getAllowedName(line.split("mtllib ").last())}"
            } else {
                line
            }
        }
    }

    fun getPackContainerPath() = "$resourceType/$id"
    fun getPackContainerPath(type: String) = "$type/$resourceType/$id"

    fun createJsonModel(): Model {
        if (type == "obj") return createObjJsonModel()
        else TODO("Доступны только .obj модели")
    }

    fun createObjJsonModel(): Model {
        return Model(
            parent = OBJ_LOADER_PARENT,
            model = getPackPath("models/${getPackContainerPath()}/${id}.obj"),
            display = display,
        )
    }

    fun getModelFile(): File {
        return FileSystem.ASSETS.resolve("$model.$type")
    }

    fun getTextures(): List<File> {
        return materialTextures.values.map { FileSystem.ASSETS.resolve("$it.png") }
    }
}