package org.qbrp.client.render.ui.interaction.event

import org.qbrp.main.engine.containers.util.ContainerId

interface InventorySelection {
    val selectedInventory: ContainerId?
}