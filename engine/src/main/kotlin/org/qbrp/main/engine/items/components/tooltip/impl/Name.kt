package org.qbrp.main.engine.items.components.tooltip.impl

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.engine.items.components.tooltip.NameComponent

class Name(val text: String): NameComponent() {
    override fun getName(playerObject: PlayerObject): String = text
}