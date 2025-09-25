package org.qbrp.client.engine.inventory.service

import org.qbrp.client.render.ui.view.View
import org.qbrp.main.engine.containers.util.ContainerId

class InventoryCloseListener(private val inventoryManager: InventoryManagerImpl) {
    fun onInventoryClosed(view: View, id: ContainerId) {
        inventoryManager.close(id)
    }
}