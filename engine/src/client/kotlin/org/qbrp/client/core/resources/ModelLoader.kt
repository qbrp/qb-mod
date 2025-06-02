package org.qbrp.client.core.resources

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule

@Autoload(env = EnvType.CLIENT)
class ModelLoader: QbModule("model-loader") {
    override fun getKoinModule() = module {
        single<ModelRepository> { ModelRepositoryImpl(mutableMapOf()) }
    }

    override fun getAPI(): ModelRepository = get()
}