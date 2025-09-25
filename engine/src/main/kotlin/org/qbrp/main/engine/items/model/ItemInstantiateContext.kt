package org.qbrp.main.engine.items.model

import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.ItemsModule

class ItemInstantiateContext(val module: ItemsModule) {
    companion object {
        val DEFAULT = ItemInstantiateContext(Engine.getModule())
    }
}