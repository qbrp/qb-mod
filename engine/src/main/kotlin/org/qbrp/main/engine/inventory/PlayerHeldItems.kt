package org.qbrp.main.engine.inventory

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.ServerPlayerObject

interface PlayerHeldItems {
    fun getHeldItem(player: ServerPlayerObject): InventoryEntry?
    fun setHeldItem(player: ServerPlayerObject, item: InventoryEntry?)
}