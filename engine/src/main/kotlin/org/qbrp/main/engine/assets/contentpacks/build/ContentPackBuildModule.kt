package org.qbrp.main.engine.assets.contentpacks.build

import org.koin.core.component.get
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.assets.contentpacks.patches.PatchesAPI
import org.qbrp.main.engine.assets.resourcepack.ResourcePackAPI
import java.io.File

@Autoload(Priorities.MODULE)
class ContentPackBuildModule: EngineModule("content-pack-build"), ContentPackBuildAPI {
    init {
        dependsOn { Engine.isApiAvailable<PatchesAPI>() }
        dependsOn { Engine.isApiAvailable<ResourcePackAPI>() }
    }

    override fun getKoinModule() = onlyApi<ContentPackBuildAPI>(this)

    override fun build(file: File, version: String): ContentPack {
        file.deleteRecursively()
        val nodes = get<ResourcePackAPI>().scanNodes()
        val overrides = get<ResourcePackAPI>().scanOverrides()
        return ContentPackBuilder(file, get())
            .bakeResourcePack(nodes, overrides)
            .bakeModelsList(nodes, overrides)
            .bakeManifest(version)
            .build()
    }
}