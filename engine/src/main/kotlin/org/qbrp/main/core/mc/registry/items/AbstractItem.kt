package org.qbrp.main.core.mc.registry.items

import net.minecraft.item.Item
import org.qbrp.main.engine.items.ItemsModule

class AbstractItem(): Item(SETTINGS) {
    companion object{
        val SETTINGS = Settings().maxCount(1)
    }
}