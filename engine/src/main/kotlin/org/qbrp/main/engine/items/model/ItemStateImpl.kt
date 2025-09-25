package org.qbrp.main.engine.items.model

import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext

open class ItemStateImpl() : ComponentState<ItemObject>(), ItemState {
    override val componentContext: ItemComponentInstantiateContext get() = ItemComponentInstantiateContext(this)
}