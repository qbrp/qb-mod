package org.qbrp.main.engine.containers.v2.system

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry

interface ContainerCache {
    val itemToContainerMap: Map<ContainerEntry, ContainerId>
    fun add(container: ContainerId, entry: ContainerEntry)
    fun remove(container: ContainerId, entry: ContainerEntry): Boolean
    fun invalidate(container: ContainerId)
    fun get(containerId: ContainerId): List<ContainerEntry>
}