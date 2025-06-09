package org.qbrp.main.engine.items.components.tooltip.impl

import kotlinx.serialization.Serializable
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.engine.items.components.tooltip.NameComponent

/** Статическое имя, отображаемое для всех игроков одинаково **/
@Serializable
class Name(val text: String): NameComponent() {
    override fun getName(playerObject: ServerPlayerObject): String = text
}