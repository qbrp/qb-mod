package org.qbrp.main.core.mc.registry

import org.koin.dsl.module
import org.qbrp.main.core.mc.registry.groups.InventoryGroupsRegistry
import org.qbrp.main.core.mc.registry.items.ItemRegistry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule

@Autoload(LoadPriority.HIGHEST)
class GameRegistries(): QbModule("registries") {

    override fun getKoinModule() = module {
        single { ItemRegistry() }
        // single { InventoryGroupsRegistry() }
    }
}