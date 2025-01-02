package org.qbrp.core.resources.parsing.filters

import java.io.File

class ExtensionFilter(private val extensions: Set<String>) : FileFilter {
    override fun matches(file: File): Boolean {
        return file.extension.lowercase() in extensions
    }
}