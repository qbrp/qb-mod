package org.qbrp.main.engine.items.components

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.InstantiationContext
import org.qbrp.main.engine.items.model.ItemState

class ItemComponentInstantiateContext(override val componentManager: ItemState) : InstantiationContext<ItemState>, ItemState by componentManager