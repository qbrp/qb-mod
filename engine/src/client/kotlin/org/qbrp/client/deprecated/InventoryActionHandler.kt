package org.qbrp.client.deprecated

import org.qbrp.main.engine.containers.util.ContainerId

interface InventoryActionHandler {
    fun takeItem(slot: Int, containerId: ContainerId)
    fun putItem(slot: Int, containerId: ContainerId)
    fun swapItem(slot: Int, containerId: ContainerId)
}