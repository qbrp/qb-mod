package org.qbrp.client.controls

import org.qbrp.client.render.ui.interaction.event.InventorySelection
import org.qbrp.client.render.ui.interaction.event.InventorySelectionCallback
import org.qbrp.client.render.ui.interaction.event.ItemSelection
import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.EventType
import org.qbrp.main.core.events.collect
import org.qbrp.main.core.events.collectLists
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.items.model.ItemId

/**
 * Триггерится при желании игрока контактировать с системой взаимодействий.
 * */
data class ClientInteractionCallEvent(
    val selectedInventory: ContainerId?,
    val selectedItem: ItemId?
) : Event

data class InteractionContextMenuApplyEvent(
    val contextMenuViewData: InteractionContextMenuViewData
) : Event


data class InteractionModeApplyEvent(
    val interactionMode: InteractionMode
) : Event

data class NoClipApplyEvent(
    val update: Boolean
) : Event

data class SelectInteractionEvent(
    val id: InteractionId
) : Event

val ITEM_SELECT = EventType<InventorySelectionCallback, Collection<ItemSelection>>("item_select")

fun getSelectedItem(): ItemId? {
    return ITEM_SELECT
        .collectLists(InventorySelectionCallback())
        .firstNotNullOfOrNull { it.selectedItem } ?: return null
}

val INVENTORY_SELECT = EventType<InventorySelectionCallback, InventorySelection>("inventory_select")

fun getSelectedInventory(): ContainerId? {
    return INVENTORY_SELECT
        .collect(InventorySelectionCallback())
        .firstOrNull()
        ?.selectedInventory
}

