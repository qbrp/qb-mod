package org.qbrp.main.engine.inventory.networking

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.entity.OccupancyView

interface InventoryListener {
    fun onItems(entries: List<ContainerEntry>, id: ContainerId)
    fun onName(id: ContainerId, name: ContainerDisplayName)
    fun onOccupancy(id: ContainerId, occupancy: OccupancyView)
}