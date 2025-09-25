package org.qbrp.client.engine.inventory.events

import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.main.core.events.Event

data class InventoryCloseEvent(val container: ClientContainerState): Event