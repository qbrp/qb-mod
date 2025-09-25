package org.qbrp.main.engine.inventory.entity

import org.qbrp.main.engine.containers.util.ContainerId

data class Inventory(val containerId: ContainerId, val params: InventoryParams = InventoryParams())