package org.qbrp.main.core.mc.registry

import org.koin.core.component.get
import org.qbrp.main.core.mc.registry.items.AbstractItem
import org.qbrp.main.core.mc.registry.items.ItemDefinition
import org.qbrp.main.core.mc.registry.items.ItemRegistry

class ServerGameRegistries: GameRegistries() {
    override fun onEnable() {
        get<ItemRegistry>().apply {
            registerItem(ItemDefinition("undefined", AbstractItem()))
        }
    }
}