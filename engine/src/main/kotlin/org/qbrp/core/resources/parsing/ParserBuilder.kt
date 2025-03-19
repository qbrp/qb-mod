package org.qbrp.core.resources.parsing

import com.google.gson.Gson
import org.qbrp.core.resources.parsing.filters.FileFilter
import org.qbrp.core.resources.structure.Branch
import org.qbrp.core.resources.units.ContentUnit
import java.io.File

class ParserBuilder {
    private var gson: Gson = Gson()
    private var maxDepth: Int = Int.MAX_VALUE
    private val filters = mutableListOf<FileFilter>()
    private var clazz: Class<*> = ContentUnit::class.java
    private var onOpen: (File, ContentUnit, Branch) -> kotlin.Unit = { _, _, _ -> }

    fun setMaxDepth(depth: Int): ParserBuilder {
        maxDepth = depth
        return this
    }

    fun setGson(gson: Gson): ParserBuilder {
        this.gson = gson
        return this
    }

    fun setClass(clazz: Class<*>): ParserBuilder {
        this.clazz = clazz
        return this
    }

    fun addFilter(filter: FileFilter): ParserBuilder {
        filters.add(filter)
        return this
    }

    fun setOnOpen(onOpen: (File, ContentUnit, Branch) -> kotlin.Unit): ParserBuilder {
        this.onOpen = onOpen
        return this
    }

    // Строим Parser
    fun build(): Parser {
        return Parser(gson, filters, maxDepth, clazz, onOpen)
    }
}
