package org.qbrp.client.engine.inventory.events

import org.qbrp.client.engine.inventory.model.InventoryDefinition
import org.qbrp.main.core.events.Event
import org.qbrp.main.network.inventory.ScreenCorner

data class InventoryOpenEvent(val containers: List<InventoryDefinition>, val screenCorner: ScreenCorner) : Event