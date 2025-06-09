package org.qbrp.client.engine.items.components.inventory

import org.qbrp.client.engine.inventory.model.AbstractContainerHolder
import org.qbrp.client.engine.inventory.model.ClientInventoryEntry
import org.qbrp.main.engine.items.components.ItemBehaviour

open class ItemContainerHolder(
    override val id: String,
    override var entries: List<ClientInventoryEntry> = listOf(),
) : ItemBehaviour(), AbstractContainerHolder {

}