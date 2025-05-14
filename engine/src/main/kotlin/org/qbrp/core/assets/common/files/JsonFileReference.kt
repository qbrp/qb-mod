package org.qbrp.core.assets.common.files

import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.common.Asset
import org.qbrp.core.assets.common.AssetKey
import org.qbrp.core.assets.common.Key
import org.qbrp.core.game.serialization.GameMapper
import java.io.File

class JsonFileReference<T : Asset>(override val key: Key, private val clazz: Class<T>) : FileReference<T> {
    val path = key.getId() + ".json"

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
        val MAPPER = GameMapper
    }
}
