package org.qbrp.client.engine.inventory.model

import org.qbrp.main.network.inventory.InventoryParams

class InventoryDefinition(
    val container: ClientContainerState,
    val params: InventoryParams = InventoryParams.DEFAULT
)