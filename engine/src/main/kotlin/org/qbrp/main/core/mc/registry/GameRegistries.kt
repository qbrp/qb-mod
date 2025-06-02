package org.qbrp.main.core.mc.registry

import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.mc.registry.groups.InventoryGroupsRegistry
import org.qbrp.main.core.mc.registry.items.ItemDefinition
import org.qbrp.main.core.mc.registry.items.ItemRegistry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.engine.items.QbItem

@Autoload(both = true, priority = LoadPriority.HIGHEST)
class GameRegistries(): QbModule("registries") {

    override fun getKoinModule() = module {
        single { ItemRegistry() }
        // single { InventoryGroupsRegistry() }
    }

    override fun onLoad() {
        get<ItemRegistry>().apply {
            registerItem(ItemDefinition("abstract_item", QbItem()))
            registerItem(ItemDefinition("undefined", QbItem()))
        }
    }
}