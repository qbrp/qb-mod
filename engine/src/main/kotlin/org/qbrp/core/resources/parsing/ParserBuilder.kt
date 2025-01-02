package org.qbrp.core.resources.parsing

import com.google.gson.Gson
import org.qbrp.system.utils.keys.Key
import org.qbrp.core.resources.parsing.filters.FileFilter
import org.qbrp.core.resources.units.ContentUnit
import java.io.File

class ParserBuilder {
    private var gson: Gson = Gson()
    private var maxDepth: Int = Int.MAX_VALUE
    private val filters = mutableListOf<FileFilter>()
    private var clazz: Class<*> = ContentUnit::class.java
    private var naming: (File) -> Key = { file -> Key(file.nameWithoutExtension) } // Значение по умолчанию

    // Устанавливаем максимальную глубину
    fun setMaxDepth(depth: Int): ParserBuilder {
        maxDepth = depth
        return this
    }

    // Устанавливаем Gson
    fun setGson(gson: Gson): ParserBuilder {
        this.gson = gson
        return this
    }

    // Устанавливаем класс, с которым будет работать Parser
    fun setClass(clazz: Class<*>): ParserBuilder {
        this.clazz = clazz
        return this
    }

    // Добавляем фильтр для файлов
    fun addFilter(filter: FileFilter): ParserBuilder {
        filters.add(filter)
        return this
    }

    // Устанавливаем лямбду для создания UnitKey
    fun setNaming(naming: (File) -> Key): ParserBuilder {
        this.naming = naming
        return this
    }

    // Строим Parser
    fun build(): Parser {
        return Parser(gson, filters, maxDepth, clazz, naming)
    }
}
