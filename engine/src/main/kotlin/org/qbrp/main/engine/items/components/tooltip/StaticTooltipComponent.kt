package org.qbrp.main.engine.items.components.tooltip

import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.engine.items.ItemObject

abstract class StaticTooltipComponent: ItemDisplayComponent() {
    abstract fun provide(player: ServerPlayerObject, item: ItemObject): String
}