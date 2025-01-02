package org.qbrp.core.resources.parsing.filters

import java.io.File

class NameFilter(private val names: Set<String>) : FileFilter {
    override fun matches(file: File): Boolean {
        return file.name in names
    }
}