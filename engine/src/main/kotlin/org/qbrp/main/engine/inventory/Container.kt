package org.qbrp.main.engine.inventory

import org.qbrp.main.core.game.serialization.Identifiable

interface Container: Identifiable {
    /** @return Взятый из слота предмет **/
    fun takeItem(slot: Int): InventoryEntry?
    fun putItem(slot: Int, item: InventoryEntry): InventoryEntry?
    fun swapItem(slot: Int, item: InventoryEntry): InventoryEntry
    fun getEntries(): List<InventoryEntry>
}