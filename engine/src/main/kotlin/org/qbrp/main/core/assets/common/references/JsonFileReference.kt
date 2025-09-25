package org.qbrp.main.core.assets.common.references

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.Key
import org.qbrp.main.core.components.serialization.Serialization
import org.qbrp.main.core.utils.log.LoggerUtil
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

open class JsonFileReference<T : Asset>(override val key: Key, private val klass: KClass<T>) : FileReference<T> {
    val path = key.getId() + ".json"
    val serializer by lazy { Serialization.JSON.serializersModule.serializer(klass.createType()) }

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
        try {
            return Serialization.JSON.decodeFromString(serializer as KSerializer<T>, content)
        } catch (e: Exception) {
            LoggerUtil.get("serialization").error("Ошибка чтения json-файла (${path}): ${e.message}")
            throw e
        }
    }

    companion object {
        val MAPPER = Serialization
    }
}
