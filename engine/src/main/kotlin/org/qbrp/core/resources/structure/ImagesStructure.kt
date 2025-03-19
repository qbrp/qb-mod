package org.qbrp.core.resources.structure

import org.qbrp.core.resources.data.pack.TextureData
import org.qbrp.core.resources.parsing.ParserBuilder
import org.qbrp.core.resources.parsing.filters.ExtensionFilter
import org.qbrp.core.resources.units.ContentUnit
import org.qbrp.system.utils.keys.Key
import java.nio.file.Path

class ImagesStructure(branchName: String, branch: Branch): Structure(branch.path.toFile()) {
    val imagesBranch = addBranch(branchName)

    fun openImages(extensions: Set<String> = setOf("png")) {
        ParserBuilder()
            .setClass(TextureData::class.java)
            .addFilter(ExtensionFilter(extensions))
            .setOnOpen { file, image, branch ->
                registerContent(image, Key(file.nameWithoutExtension))
                imagesBranch.add(image)
            }
            .build()
            .parse(imagesBranch)
    }

}