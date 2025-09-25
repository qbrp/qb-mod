package org.qbrp.main.engine.containers.v2.system

import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.ContainedIn
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.event.ItemAddedInContainerEvent
import org.qbrp.main.engine.containers.v2.event.ItemRemovedFromContainerEvent
import org.qbrp.main.engine.containers.v2.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.containers.v2.system.request.MoveCallback
import org.qbrp.main.engine.props.components.PropManager
import org.qbrp.main.engine.world.location.Location
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

class ItemCommandBufferImpl : ItemCommandBuffer {
    private val queueRef = AtomicReference(ConcurrentLinkedQueue<Command>())

    sealed class Command {
        data class Add(val item: ContainerEntry, val containerId: ContainerId) : Command()
        data class Remove(val item: ContainerEntry, val container: ContainerId) : Command()
        data class Copy(val item: ContainerEntry, val toContainer: ContainerId, val prototype: ContainerEntryPrototype) : Command()
        data class SpawnProp(val item: ContainerEntry, val location: Location) : Command()
        data class Callback(val result: TransferResult, val callback: MoveCallback) : Command()
    }

    override fun add(item: ContainerEntry, containerId: ContainerId) {
        queueRef.get().add(Command.Add(item, containerId))
    }

    override fun remove(item: ContainerEntry, containerId: ContainerId) {
        queueRef.get().add(Command.Remove(item, containerId))
    }

    override fun copy(item: ContainerEntry, toContainer: ContainerId, prototype: ContainerEntryPrototype) {
        queueRef.get().add(Command.Copy(item, toContainer, prototype))
    }

    override fun spawnProp(item: ContainerEntry, location: Location) {
        queueRef.get().add(Command.SpawnProp(item, location))
    }

    override fun callback(result: TransferResult, callback: MoveCallback) {
        queueRef.get().add(Command.Callback(result, callback))
    }

    context(ctx: EventContext, registry: ComponentRegistry)
    override fun process(cache: ContainerCache) {
        val snapshot = queueRef.getAndSet(ConcurrentLinkedQueue())

        if (snapshot.isEmpty()) return

        val ordered = snapshot.toList().sortedBy {
            when (it) {
                is Command.Remove -> 0
                is Command.Add -> 1
                is Command.Copy -> 2
                is Command.SpawnProp -> 3
                is Command.Callback -> 4
            }
        }

        for (cmd in ordered) {
            when (cmd) {
                is Command.Add -> {
                    cmd.item.add(ContainedIn(cmd.containerId))
                    cache.add(cmd.containerId, cmd.item)
                    ItemAddedInContainerEvent(cmd.item, cmd.containerId).emit()
                }
                is Command.Remove -> {
                    cmd.item.remove<ContainedIn>()
                    cache.remove(cmd.container, cmd.item)
                    ItemRemovedFromContainerEvent(cmd.item, cmd.container).emit()
                }
                is Command.Copy -> {
                    val newItem = cmd.prototype.copy(cmd.item)
                    newItem.add(ContainedIn(cmd.toContainer))
                    cache.add(cmd.toContainer, newItem)
                    ItemAddedInContainerEvent(newItem, cmd.toContainer).emit() // фикс: эмитим для newItem
                }
                is Command.SpawnProp -> {
                    cmd.item.get<PropManager>()?.spawn(cmd.location)
                }
                is Command.Callback -> {
                    cmd.callback(cmd.result)
                }
            }
        }
    }
}