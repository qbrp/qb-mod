package org.qbrp.core.resources.parsing

import com.google.gson.Gson
import org.qbrp.core.resources.parsing.filters.FileFilter
import org.qbrp.core.resources.structure.Branch
import org.qbrp.core.resources.units.ContentUnit
import org.qbrp.core.resources.units.Unit
import java.io.File

class Parser(
    private val gson: Gson,
    private val filters: List<FileFilter>,
    private val maxDepth: Int,
    private val clazz: Class<*>,
    private val onOpen: (File, ContentUnit, Branch) -> kotlin.Unit // Лямбда для генерации UnitKey на основе файла
) {
    fun parse(branch: Branch): MutableList<Unit> {
        return parse(branch.path.toFile())
    }

    fun parse(path: File): MutableList<Unit> {
        val rootBranch = Branch(path.toPath())
        parseDirectory(path, rootBranch, 0)
        return rootBranch.children
    }

    private fun parseDirectory(directory: File, parentBranch: Branch, currentDepth: Int) {
        if (currentDepth > maxDepth) return

        val children = directory.listFiles()?.filter { file ->
            // Применяем фильтры только к файлам, а не к директориям
            if (file.isDirectory) return@filter true  // Директории не фильтруем
            val matches = filters.all { it.matches(file) }
            matches
        } ?: emptyList()

        for (file in children) {
            when {
                file.isDirectory -> {
                    println("Открытие файла ${file.name}")
                    val childBranch = parentBranch.addBranch(file.name)
                    parseDirectory(file, childBranch, currentDepth + 1)
                }
                file.isFile -> {
                    val unit = parentBranch.open(file.name, clazz, gson)
                    if (!parentBranch.children.contains(unit)) {
                        println("Открытие файла ${file.name}")
                        onOpen(file, unit, parentBranch)
                        parentBranch.add(unit)
                    }
                }
            }
        }
    }
}

