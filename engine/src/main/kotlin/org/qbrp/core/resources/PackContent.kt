package org.qbrp.core.resources

import org.qbrp.core.resources.content.ModelBundle
import org.qbrp.core.resources.data.pack.ModelData
import org.qbrp.core.resources.structure.PackStructure
import org.qbrp.system.utils.format.*
import java.util.UUID
import java.nio.file.Path
import kotlin.io.path.pathString

class PackContent(val packStructure: PackStructure) {
    private val modelBundles = mutableMapOf<String, ModelBundle>()

    fun addModelBundle(data: ModelData, path: Path) {
        modelBundles[path.pathString] = (ModelBundle(data, path, packStructure))
    }

    fun getModelBundle(path: String): ModelBundle? {
        return modelBundles[path]
    }

    companion object {
        private var customModelData: Int = 1
        fun genCustomModelData() = customModelData++
    }
}