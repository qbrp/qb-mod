package org.qbrp.client.core.mc.registry

import org.koin.core.component.get
import org.qbrp.client.engine.items.ClientQbItem
import org.qbrp.main.core.mc.registry.GameRegistries
import org.qbrp.main.core.mc.registry.items.ItemDefinition
import org.qbrp.main.core.mc.registry.items.ItemRegistry

class ClientGameRegistries: GameRegistries() {
    override fun onEnable() {
        get<ItemRegistry>().apply {
            registerItem(ItemDefinition("abstract_item", ClientQbItem()))
            registerItem(ItemDefinition("undefined", ClientQbItem()))
        }
    }
}