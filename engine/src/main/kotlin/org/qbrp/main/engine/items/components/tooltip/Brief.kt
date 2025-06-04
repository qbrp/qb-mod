package org.qbrp.main.engine.items.components.tooltip

import kotlinx.serialization.Serializable
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.engine.items.ItemObject

@Serializable
class Brief(val text: String): StaticTooltip() {
    override fun provide(
        player: PlayerObject,
        item: ItemObject
    ): String {
        return text
    }
}