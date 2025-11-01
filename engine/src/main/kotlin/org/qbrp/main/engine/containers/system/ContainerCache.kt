package org.qbrp.main.engine.containers.system

import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.util.ContainerId

interface ContainerCache {
    val itemToContainerMap: Map<ContainerEntry, ContainerId>
    fun add(container: ContainerId, entry: ContainerEntry)
    fun remove(container: ContainerId, entry: ContainerEntry): Boolean
    fun invalidate(container: ContainerId)
    fun get(containerId: ContainerId): List<ContainerEntry>
}