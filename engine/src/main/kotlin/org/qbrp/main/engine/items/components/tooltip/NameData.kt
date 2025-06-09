package org.qbrp.main.engine.items.components.tooltip

import org.qbrp.main.core.mc.player.ServerPlayerObject

interface NameData {
    fun getName(playerObject: ServerPlayerObject): String
}