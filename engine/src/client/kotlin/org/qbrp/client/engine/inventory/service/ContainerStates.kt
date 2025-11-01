package org.qbrp.client.engine.inventory.service

import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.entity.OccupancyView

class ContainerStates() {
    private val states: MutableMap<ContainerId, ClientContainerState> = mutableMapOf()

    fun add(
        containerId: ContainerId,
        items: List<ContainerEntry> = listOf(),
        name: ContainerDisplayName = ContainerDisplayName("Инвентарь")
    ): ClientContainerState {
        val state = ClientContainerState(containerId, name, items.toMutableList(), OccupancyView.Unlimited)
        states[containerId] = state
        return state
    }

    fun getOrAdd(containerId: ContainerId): ClientContainerState {
        return get(containerId) ?: add(containerId)
    }

    fun get(containerId: ContainerId): ClientContainerState? {
        return states[containerId]
    }
}