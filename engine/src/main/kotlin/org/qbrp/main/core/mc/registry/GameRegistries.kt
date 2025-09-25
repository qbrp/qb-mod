package org.qbrp.main.core.mc.registry

import org.koin.dsl.module
import org.qbrp.main.core.mc.registry.items.ItemRegistry
import org.qbrp.main.core.modules.EngineModule

abstract class GameRegistries: EngineModule("registries") {
    override fun getKoinModule() = module {
        single { ItemRegistry() }
    }
}