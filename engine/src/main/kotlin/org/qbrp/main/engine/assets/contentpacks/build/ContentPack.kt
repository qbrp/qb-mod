package org.qbrp.main.engine.assets.contentpacks.build

import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.engine.assets.contentpacks.PackManifest
import org.qbrp.main.engine.assets.resourcepack.ModelsList
import java.io.File

open class ContentPack(val file: File,
                       val manifest: PackManifest,
                       val modelsList: ModelsList): Asset() {
    companion object {
        const val PACK_NAME = "qbrp-pack"
        const val PACK_PROFILE = "file/qbrp-pack"
    }

    val resourcePackFile = file.resolve(PACK_NAME)
    val manifestFile = file.resolve("manifest.json")
    val version
        get() = manifest.version
}