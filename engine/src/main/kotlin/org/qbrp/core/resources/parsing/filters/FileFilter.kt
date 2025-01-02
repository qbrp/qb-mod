package org.qbrp.core.resources.parsing.filters

import java.io.File

interface FileFilter {
    fun matches(file: File): Boolean
}