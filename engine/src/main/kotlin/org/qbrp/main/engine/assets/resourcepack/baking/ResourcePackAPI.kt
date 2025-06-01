package org.qbrp.main.engine.assets.resourcepack.baking

import org.qbrp.main.core.modules.ModuleAPI
import java.io.File

interface ResourcePackAPI: ModuleAPI {
    fun bakeResourcePack(path: File)
}