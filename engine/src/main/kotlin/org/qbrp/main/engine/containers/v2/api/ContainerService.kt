package org.qbrp.main.engine.containers.v2.api

import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.Capacity
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.entity.ContainerEntity
import org.qbrp.main.engine.containers.v2.entity.EntryReference
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.system.request.Destination
import org.qbrp.main.engine.containers.v2.system.request.MoveCallback
import org.qbrp.main.engine.containers.v2.system.request.MoveMode
import org.qbrp.main.engine.containers.v2.entity.OccupancyView

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
        containerId: ContainerId = ContainerId.random()
    ): ContainerId

    fun requestMove(item: ContainerEntry, destination: Destination, moveMode: MoveMode = MoveMode.Transfer, callback: MoveCallback = {}, required: Boolean = false)

    fun addProvider(provider: Storage<out ContainerEntity>)
}