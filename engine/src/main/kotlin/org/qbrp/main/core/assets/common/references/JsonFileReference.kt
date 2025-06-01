package org.qbrp.main.core.assets.common.references

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.Key
import org.qbrp.main.core.game.serialization.GameMapper
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

open class JsonFileReference<T : Asset>(override val key: Key, private val klass: KClass<T>) : FileReference<T> {
    val path = key.getId() + ".json"
    val serializer by lazy { GameMapper.JSON.serializersModule.serializer(klass.createType()) }

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

    private fun serialize(data: T): String = MAPPER.JSON.encodeToString(serializer, data)

    private fun deserialize(content: String): T {
        @Suppress("UNCHECKED_CAST")
        return GameMapper.JSON.decodeFromString(serializer as KSerializer<T>, content)
    }

    companion object {
        val MAPPER = GameMapper
    }
}
