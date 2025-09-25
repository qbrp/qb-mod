package org.qbrp.main.engine.inventory.player

import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.inventory.entity.Inventory
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.inventory.entity.ScreenCorner

interface PlayerInventoryManager {
    val openedGroups: Collection<InventoryGroup>
    val openedInventories: Collection<Inventory>
    fun getInventory(id: ContainerId): Inventory?
    fun openGroup(group: InventoryGroup)
    fun closeGroup(group: InventoryGroup)
    fun open(inventory: Inventory, corner: ScreenCorner = ScreenCorner.TOP_LEFT)
    fun open(container: ContainerId) {
        open(Inventory(container))
    }
    fun <I: ContainerEntry> getItem(id: StringId): I?
}