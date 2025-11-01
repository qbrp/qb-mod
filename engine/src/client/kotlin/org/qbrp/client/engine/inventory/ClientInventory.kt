package org.qbrp.client.engine.inventory

import org.qbrp.client.engine.inventory.service.InventoryManager
import org.qbrp.main.engine.players.characters.entity.CharacterData

interface ClientInventory {
    val manager: InventoryManager

    fun createInventories()
    fun createCharacterInventories(character: CharacterData)
}