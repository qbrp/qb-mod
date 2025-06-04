package org.qbrp.main.engine.items.components.tooltip

import org.qbrp.main.engine.items.components.ItemBehaviour

abstract class Tooltip: ItemBehaviour() {
    protected lateinit var tooltipManager: TooltipManager
    override fun onEnable() {
        tooltipManager = requireState().getComponentOrAdd { TooltipManager() }
    }
}