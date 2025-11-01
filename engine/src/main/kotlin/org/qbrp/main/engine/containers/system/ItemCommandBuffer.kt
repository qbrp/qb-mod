package org.qbrp.main.engine.containers.system

import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.containers.system.request.MoveCallback
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.world.location.Location

interface ItemCommandBuffer {
    fun add(item: ContainerEntry, containerId: ContainerId)
    fun remove(item: ContainerEntry, containerId: ContainerId)
    fun copy(item: ContainerEntry, toContainer: ContainerId, prototype: ContainerEntryPrototype)
    fun spawnProp(item: ContainerEntry, location: Location)
    fun callback(result: TransferResult, callback: MoveCallback)
    fun process(cache: ContainerCache)
}