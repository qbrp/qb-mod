package org.qbrp.main.core.mc.registry

import net.minecraft.item.Item
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.mc.registry.groups.InventoryGroupsRegistry
import org.qbrp.main.core.mc.registry.items.ItemDefinition
import org.qbrp.main.core.mc.registry.items.ItemRegistry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.engine.items.QbItem

class ServerGameRegistries: GameRegistries() {
    override fun onEnable() {
        get<ItemRegistry>().apply {
            registerItem(ItemDefinition("abstract_item", QbItem()))
            registerItem(ItemDefinition("undefined", QbItem()))
        }
    }
}