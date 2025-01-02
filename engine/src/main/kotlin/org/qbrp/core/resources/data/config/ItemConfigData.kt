package org.qbrp.core.resources.data.pack

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.qbrp.core.resources.data.Data
import java.io.File

data class ItemConfigData(private var models: Map<String, Model> = emptyMap()) : Data() {

    data class Tag(val itemModel: Model,
                   val tagModel: Model.TagModel,
                   val key: String) {
        fun getItemRender(): ItemRender? {
            return tagModel.components.find { it.type == "meta.ItemRender" }
                ?.let { gson.fromJson(gson.toJson(it.data), ItemRender::class.java) }
        }

        data class ItemRender(val renderType: String,
                              val path: String,
                              val autoUpdate: String)
    }

    data class Model(val id: String,
                     val category: String,
                     val tags: List<TagModel>) {

        data class TagModel(val id: String,
                            val components: List<ComponentModel>) {

            data class ComponentModel(val type: String,
                                      val data: JsonObject)
        }

        fun getKey(tag: String = ""): String = "$category:$id${if (tag.isNotEmpty()) "#$tag" else ""}"
    }

    override fun toFile(): String = gson.toJson(this)

    fun getTags(): Map<String, Tag> {
        val tagMap = mutableMapOf<String, Tag>()
        models.values.forEach { model ->
            model.tags.forEach { tag ->
                val key = model.getKey(tag.id)
                tagMap[key] = Tag(model, tag, key)
            }
        }
        return tagMap
    }

    companion object {
        fun fromFile(file: File): ItemConfigData {
            val fileName = file.nameWithoutExtension
            val directory = file.parentFile.name

            val content = file.readText(Charsets.UTF_8)
                .replace("%file_name%", fileName)
                .replace("%directory%", directory)

            val json = JsonParser.parseString(content).asJsonObject
            val data = ItemConfigData()

            val models = mutableMapOf<String, Model>()
            json.entrySet().forEach { (key, value) ->
                val model = gson.fromJson(value, Model::class.java)
                models[model.getKey()] = model
            }

            data.models = models
            println(data)
            return data
        }
    }
}
