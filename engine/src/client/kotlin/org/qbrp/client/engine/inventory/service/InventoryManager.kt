package org.qbrp.client.engine.inventory.service

import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.client.engine.inventory.model.InventoryDefinition
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.network.inventory.ScreenCorner

interface InventoryManager {
    val openContainers: List<ClientContainerState>
    fun openGroup(definitions: List<InventoryDefinition>, corner: ScreenCorner = ScreenCorner.TOP_LEFT)
    fun close(id: ContainerId)
    fun closeAll()
}