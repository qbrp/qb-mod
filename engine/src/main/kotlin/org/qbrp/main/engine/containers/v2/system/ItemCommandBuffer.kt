package org.qbrp.main.engine.containers.v2.system

import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.containers.v2.system.request.MoveCallback
import org.qbrp.main.engine.world.location.Location

interface ItemCommandBuffer {
    fun add(item: ContainerEntry, containerId: ContainerId)
    fun remove(item: ContainerEntry, containerId: ContainerId)
    fun copy(item: ContainerEntry, toContainer: ContainerId, prototype: ContainerEntryPrototype)
    fun spawnProp(item: ContainerEntry, location: Location)
    fun callback(result: TransferResult, callback: MoveCallback)
    context(ctx: EventContext, registry: ComponentRegistry) fun process(cache: ContainerCache)
}