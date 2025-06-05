package org.qbrp.main.engine.items.components.tooltip

import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.items.components.tooltip.impl.ItemDisplay

abstract class ItemDisplayComponent: ItemBehaviour() {
    protected lateinit var tooltipManager: ItemDisplay
    override fun onEnable() {
        tooltipManager = requireState().getComponentOrAdd { ItemDisplay() }
    }
}