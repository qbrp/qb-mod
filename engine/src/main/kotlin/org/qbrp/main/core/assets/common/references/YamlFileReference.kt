package org.qbrp.main.core.assets.common.references

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.Key
import java.io.File

class YamlFileReference<T : Asset>(override val key: Key, private val clazz: Class<T>) : FileReference<T> {
    val path = key.getId() + ".yml"

    override fun exists(): Boolean {
        val file = File(path)
        return file.exists()
    }

    override fun read(): T {
        val file = File(path)
        val fileContent = file.readText()
        return deserialize(fileContent) as T
    }

    override fun write(data: T) {
        val file = FileSystem.getOrCreate(File(path))
        val content = serialize(data)  // Сериализация данных
        file.writeText(content)
    }

    private fun serialize(data: T): String = MAPPER.writeValueAsString(data)

    private fun deserialize(content: String): T {
        return MAPPER.readValue(content, clazz)
    }

    companion object {
        val MAPPER = ObjectMapper(YAMLFactory()).apply { registerKotlinModule() }
    }
}
