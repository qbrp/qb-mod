package org.qbrp.main.engine.items.model

import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.construct.CtxComponentBuilderOperator
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext

interface ItemState: AssociatedComponentState<ItemObject>, CtxComponentBuilderOperator<ItemComponentInstantiateContext>