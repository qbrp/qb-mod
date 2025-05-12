package org.qbrp.core.resources.content

import org.qbrp.core.resources.data.pack.ModelData
import org.qbrp.core.resources.data.pack.PredicatesData
import org.qbrp.core.resources.structure.PackStructure
import org.qbrp.core.resources.PackContent
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.units.TextUnit
import org.qbrp.system.utils.format.*
import java.nio.file.Path
import java.nio.file.Paths

class ModelBundle(
    model: ModelData,
    directory: Path,
    val packStructure: PackStructure
) {
    val customModelData = PackContent.genCustomModelData()

    val textures: List<String> = TODO()
    val modelUnit = packStructure.addModel(model).apply {
        (data as ModelData).processTextures(textures)
        save()
    }
}
