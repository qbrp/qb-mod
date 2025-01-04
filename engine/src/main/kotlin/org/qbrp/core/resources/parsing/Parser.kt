package org.qbrp.core.resources.parsing

import com.google.gson.Gson
import org.qbrp.system.utils.keys.Key
import org.qbrp.core.resources.parsing.filters.FileFilter
import org.qbrp.core.resources.structure.Branch
import org.qbrp.core.resources.units.Unit
import java.io.File

class Parser(
    private val gson: Gson,
    private val filters: List<FileFilter>,
    private val maxDepth: Int,
    private val clazz: Class<*>,
    private val naming: (File) -> Key // Лямбда для генерации UnitKey на основе файла
) {
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
                    val childBranch = parentBranch.addBranch(file.name)
                    parseDirectory(file, childBranch, currentDepth + 1)
                }
                file.isFile -> {
                    val unitKey = naming(file)  // Генерируем UnitKey с помощью лямбды
                    val unit = parentBranch.open(file.name, clazz, gson)
                    if (!parentBranch.children.contains(unit)) {
                        parentBranch.add(unit)
                    }
                }
            }
        }
    }
}

