package org.qbrp.deprecated.resources.parsing.filters

import java.io.File

interface FileFilter {
    fun matches(file: File): Boolean
}