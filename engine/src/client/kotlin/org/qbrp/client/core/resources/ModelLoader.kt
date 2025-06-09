package org.qbrp.client.core.resources

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents
import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule

@Autoload(env = EnvType.CLIENT)
class ModelLoader: QbModule("model-loader") {
    override fun onEnable() {
        SpecialModelLoaderEvents.LOAD_SCOPE.register { location ->
            Core.MOD_ID == location.namespace
        }
    }

    override fun getKoinModule() = module {
        single<ModelRepository> { ModelRepositoryImpl(mutableListOf()) }
    }

    override fun getAPI(): ModelRepository = get()
}