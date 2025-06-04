package org.qbrp.main.engine.items.components.tooltip

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.engine.items.ItemObject

abstract class StaticTooltip: Tooltip() {
    abstract fun provide(player: PlayerObject, item: ItemObject): String
}