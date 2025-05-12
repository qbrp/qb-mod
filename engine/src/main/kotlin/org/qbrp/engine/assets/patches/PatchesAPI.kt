package org.qbrp.engine.assets.patches

import org.qbrp.system.modules.ModuleAPI
import java.io.File

interface PatchesAPI: ModuleAPI {
    fun generateManifest(path: File, version: String, relative: String = path.path): Manifest
    fun generateChangesPatch(copyPath: File, outputPath: File, diff: DiffResult)
}