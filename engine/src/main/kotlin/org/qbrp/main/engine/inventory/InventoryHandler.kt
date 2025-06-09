package org.qbrp.main.engine.inventory

import org.qbrp.main.core.mc.player.ServerPlayerObject

interface InventoryHandler {
    fun handleTakeItem(inventoryId: String, slot: Int, player: ServerPlayerObject): InventoryEntry?
    fun handlePutItem(inventoryId: String, item: InventoryEntry, slot: Int, player: ServerPlayerObject): InventoryEntry?
    fun handleSwapItem(inventoryId: String, item: InventoryEntry, slot: Int, player: ServerPlayerObject): InventoryEntry
}