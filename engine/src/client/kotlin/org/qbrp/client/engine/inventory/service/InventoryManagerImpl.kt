package org.qbrp.client.engine.inventory.service

import org.qbrp.client.core.networking.v2.ClientPacketSender
import org.qbrp.client.engine.inventory.events.InventoryCloseEvent
import org.qbrp.client.engine.inventory.events.InventoryOpenEvent
import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.client.engine.inventory.model.InventoryDefinition
import org.qbrp.client.engine.inventory.networking.requestSync
import org.qbrp.main.engine.inventory.entity.InventoryParams
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.inventory.entity.ScreenCorner

class InventoryManagerImpl(private val events: EventContext, private val packetSender: ClientPacketSender): InventoryManager {
    override val openContainers: List<ClientContainerState> get() = containersMap.values.toList()
    private val containersMap: MutableMap<ContainerId, ClientContainerState> = HashMap()

    override fun openGroup(definitions: List<InventoryDefinition>, corner: ScreenCorner) = with(events) {
        val added = definitions.filter { open(it.container) }
        if (added.isNotEmpty()) InventoryOpenEvent(added, corner).emit()
    }

    private fun open(containerState: ClientContainerState): Boolean {
        val id = containerState.id
        if (containersMap.containsKey(id)) return false
        containersMap[id] = containerState
        containerState.requestSync(packetSender)
        return true
    }

    override fun close(id: ContainerId): Unit = with(events) {
        emitClose(id)?.let { container ->
            InventoryCloseEvent(container).emit()
        }
    }

    internal fun emitClose(id: ContainerId): ClientContainerState? {
        return containersMap.remove(id)
    }

    override fun closeAll() {
        containersMap.keys.toList().forEach { close(it) }
    }
}