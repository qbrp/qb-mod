package org.qbrp.main.engine.containers.api

import org.qbrp.main.core.util.storage.Storage
import org.qbrp.main.engine.containers.components.Capacity
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.components.Rules
import org.qbrp.main.engine.containers.entity.ContainerEntity
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.entity.EntryReference
import org.qbrp.main.engine.containers.entity.OccupancyView
import org.qbrp.main.engine.containers.system.request.Destination
import org.qbrp.main.engine.containers.system.request.MoveCallback
import org.qbrp.main.engine.containers.system.request.MoveMode
import org.qbrp.main.engine.containers.util.ContainerId

interface ContainerService {
    fun getItems(containerId: ContainerId): List<ContainerEntry>
    fun getCapacity(containerId: ContainerId): Capacity
    fun getName(containerId: ContainerId): ContainerDisplayName
    fun getOccupancy(containerId: ContainerId): OccupancyView

    fun canApply(containerId: ContainerId, entry: ContainerEntry): Boolean

    fun getAnchoredEntity(containerId: ContainerId): ContainerEntity?

    fun newContainer(
        name: ContainerDisplayName,
        capacity: Capacity = Capacity.Infinity,
        items: List<EntryReference> = listOf(),
        containerId: ContainerId = ContainerId.random(),
        rules: Rules? = null
    ): ContainerId

    fun requestMove(item: ContainerEntry, destination: Destination, moveMode: MoveMode = MoveMode.Transfer, callback: MoveCallback = {}, required: Boolean = false)

    fun addProvider(provider: Storage<out ContainerEntity>)
}