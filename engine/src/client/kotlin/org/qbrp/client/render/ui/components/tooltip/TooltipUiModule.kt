package org.qbrp.client.render.ui.components.tooltip

import net.fabricmc.api.EnvType
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(7, env = EnvType.CLIENT)
class TooltipUiModule: EngineModule("tooltip-ui") {
    override fun getKoinModule(): Module = module {
        single { TooltipManager(get(), get(), get(), get()) }
    }
}