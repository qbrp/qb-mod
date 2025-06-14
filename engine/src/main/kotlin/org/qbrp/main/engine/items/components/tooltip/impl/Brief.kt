package org.qbrp.main.engine.items.components.tooltip.impl

import kotlinx.serialization.Serializable
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.engine.items.ItemObject
import org.qbrp.main.engine.items.components.tooltip.StaticTooltipComponent

@Serializable
/** Краткое описание предмета, отображается для всех игроков одинаково. **/
class Brief(val text: String): StaticTooltipComponent() {
    override fun provide(
        player: ServerPlayerObject,
        item: ItemObject
    ): String {
        return text
    }
}