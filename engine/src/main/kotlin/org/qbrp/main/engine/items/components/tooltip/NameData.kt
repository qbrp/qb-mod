package org.qbrp.main.engine.items.components.tooltip

import org.qbrp.main.core.mc.player.PlayerObject

interface NameData {
    fun getName(playerObject: PlayerObject): String
}