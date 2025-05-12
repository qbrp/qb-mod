package org.qbrp.engine.assets.resourcepack.baking

import org.qbrp.system.modules.ModuleAPI
import java.io.File

interface ResourcePackAPI: ModuleAPI {
    fun bakeResourcePack(path: File)
}