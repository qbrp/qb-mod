package org.qbrp.main.engine.controls.entity.call

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

class InteractionRequestContext(val player: ServerPlayerObject, val hoveredInventoryContainerId: ContainerId?, val inventoryHoveredItem: ItemObject?) {
    val inventoryOpened: Boolean get() = hoveredInventoryContainerId != null
}