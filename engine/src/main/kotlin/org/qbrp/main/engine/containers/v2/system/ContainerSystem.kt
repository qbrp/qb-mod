package org.qbrp.main.engine.containers.v2.system

import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.Capacity
import org.qbrp.main.engine.containers.v2.components.ContainedIn
import org.qbrp.main.engine.containers.v2.components.ContainerAnchor
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.components.volume
import org.qbrp.main.engine.containers.v2.entity.ContainerEntity
import org.qbrp.main.engine.containers.v2.entity.EntryReference
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.system.request.Destination
import org.qbrp.main.engine.containers.v2.system.request.EntryMove
import org.qbrp.main.engine.containers.v2.system.request.MoveCallback
import org.qbrp.main.engine.containers.v2.system.request.MoveMode
import org.qbrp.main.engine.containers.v2.entity.OccupancyView
import org.qbrp.main.engine.items.model.ItemInstantiateContext
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemRepository
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.physics.model.agent.Agent
import java.util.LinkedList
import java.util.Queue

class ContainerSystem(
    private val transferSystem: TransferSystem,
    private val commandBuffer: ItemCommandBuffer,
    private val itemStorage: ItemStorage<ServerItemObject>,
    private val itemRepository: ItemRepository,
    private val containerEntitiesStorage: ContainerEntityStorage,
    private val itemCache: ContainerCache
) : ContainerService {
    private val capacities: MutableMap<ContainerId, Capacity> = mutableMapOf()
    private val displayNames: MutableMap<ContainerId, ContainerDisplayName> = mutableMapOf()
    private val requests: Queue<EntryMove> = LinkedList()

    context(ctx: EventContext, registry: ComponentRegistry)
    fun update() {
        val containerItems = itemCache.itemToContainerMap
        transferSystem.update(requests, capacities, containerItems)
        commandBuffer.process(itemCache)
    }

    override fun addProvider(provider: Storage<out ContainerEntity>) {
        containerEntitiesStorage.addStorage(provider as Storage<ContainerEntity>)
    }

    override fun canApply(containerId: ContainerId, entry: ContainerEntry): Boolean {
        return transferSystem.canAccept(
            getCapacity(containerId),
            transferSystem.occupancy(containerId, itemCache),
            entry
        )
    }

    override fun getAnchoredEntity(containerId: ContainerId): ContainerEntity? {
        return containerEntitiesStorage.find { it.filterIsInstance<ContainerAnchor>().any { it.id == containerId } }
    }

    override fun getOccupancy(containerId: ContainerId): OccupancyView {
        val items = itemCache.get(containerId)
        val capacity = getCapacity(containerId)
        return when (capacity) {
            is Capacity.FixedMass -> {
                val itemsMass = items.sumOf { it.get<Agent>()?.mass ?: 0.0 }
                OccupancyView.Limited(itemsMass / capacity.mass)
            }
            is Capacity.Physical -> {
                val itemsVolume = items.sumOf { it.get<Agent>()?.volume ?: 0.0 }
                OccupancyView.Limited(itemsVolume / capacity.volume)
            }
            is Capacity.Infinity -> OccupancyView.Unlimited
        }
    }

    override fun getItems(containerId: ContainerId): List<ContainerEntry> {
        return itemCache.get(containerId)
    }

    override fun getCapacity(containerId: ContainerId): Capacity {
        return capacities[containerId]!!
    }

    override fun getName(containerId: ContainerId): ContainerDisplayName {
        return displayNames[containerId]!!
    }

    override fun newContainer(
        name: ContainerDisplayName,
        capacity: Capacity,
        items: List<EntryReference>,
        containerId: ContainerId
    ): ContainerId {
        capacities[containerId] = capacity
        displayNames[containerId] = name
        val containedItems = itemStorage.filter { it.get<ContainedIn>()?.id == containerId }
        containedItems.forEach { itemCache.add(containerId, it) }
        items.forEach { id ->
            itemRepository.getByIdOrLoad(id.stringId, ItemInstantiateContext.DEFAULT, {
                itemCache.add(containerId, it)
            })
        }
        return containerId
    }

    override fun requestMove(
        item: ContainerEntry,
        destination: Destination,
        moveMode: MoveMode,
        callback: MoveCallback,
        required: Boolean
    ) {
        requests += EntryMove(item, destination, moveMode, callback, required)
    }
}