package org.qbrp.main.core.modules

import java.io.File

interface ModuleFileSystemAPI {
    fun createModuleFile(name: String)
    fun getModuleFile(name: String): File
    fun getModuleFile(module: QbModule): File
}