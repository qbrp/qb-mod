package org.qbrp.client.core.registry

import net.minecraft.item.Item
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.client.engine.items.ClientQbItem
import org.qbrp.main.core.Core
import org.qbrp.main.core.mc.registry.GameRegistries
import org.qbrp.main.core.mc.registry.items.ItemDefinition
import org.qbrp.main.core.mc.registry.items.ItemRegistry
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.engine.items.QbItem

class ClientGameRegistries: GameRegistries() {
    override fun onEnable() {
        get<ItemRegistry>().apply {
            registerItem(ItemDefinition("abstract_item", ClientQbItem()))
            registerItem(ItemDefinition("undefined", ClientQbItem()))
        }
    }
}