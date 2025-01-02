package org.qbrp.core.resources.data.pack
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.qbrp.core.resources.data.Data
import java.io.File

class ModelData(val json: JsonObject) : Data() {
    @Transient
    val textures: MutableMap<String, String> = mutableMapOf()

    init {
        val texturesJson = json.getAsJsonObject("textures")
        texturesJson?.entrySet()?.forEach { entry ->
            textures[entry.key] = entry.value.asString
        }
    }

    override fun toFile() = json.toString()

    private fun updateJson() {
        val texturesJson = JsonObject()
        textures.forEach { (key, value) ->
            texturesJson.addProperty(key, value)
        }
        json.add("textures", texturesJson)
    }

    fun addTexture(key: String, value: String) = textures.set(key, value).also { updateJson() }
    fun removeTexture(key: String) = textures.remove(key).also { updateJson() }

    fun processTextures(newTextures: List<String>) {
        textures.keys.zip(newTextures).forEach { (key, newTexturePath) ->
            textures[key] = newTexturePath
        }
        updateJson()
    }
    companion object {
        fun fromFile(file: File): ModelData {
            val parsedJson = JsonParser.parseString(file.readText()).asJsonObject
            return ModelData(parsedJson)
        }
    }
}
