package org.qbrp.client.engine.inventory.model

import org.qbrp.main.core.game.serialization.Identifiable

interface ItemHolder: Identifiable {
    fun setItems(items: List<ClientInventoryEntry>)
}