package org.qbrp.client.engine.inventory.networking

import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.client.engine.inventory.model.InventoryDefinition
import org.qbrp.client.engine.inventory.service.ContainerStates
import org.qbrp.client.engine.inventory.service.InventoryManager
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.engine.inventory.InventoriesModule
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.inventory.packet.InventoryClosePacket
import org.qbrp.main.engine.inventory.packet.InventoryGroupOpenPacket
import org.qbrp.main.engine.inventory.networking.WidgetsInventoryListener
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.entity.OccupancyView
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.inventory.networking.InventoryListener
import org.qbrp.main.engine.inventory.packet.ContainerSyncPacket

class ClientInventoriesNetworkHandler(private val packetListenerActivator: PacketListenerActivator<ClientContext>,
                                      private val containerStates: ContainerStates,
                                      private val itemStorage: ClientItemStorage,
                                      private val inventoryManager: InventoryManager): WidgetsInventoryListener, InventoryListener {
    fun run() {
        packetListenerActivator.apply {
            run(
                InventoriesModule.CLOSE,
                this@ClientInventoriesNetworkHandler,
                { buf, ctx -> InventoryClosePacket(buf) })
            run(
                InventoriesModule.OPEN,
                this@ClientInventoriesNetworkHandler,
                { buf, ctx -> InventoryGroupOpenPacket(buf) })
            run(
                InventoriesModule.ITEMS,
                this@ClientInventoriesNetworkHandler,
                { buf, ctx -> ContainerSyncPacket(buf, itemStorage) }
            )
        }
    }

    override fun onItems(entries: List<ContainerEntry>, id: ContainerId) {
        containerStates.getOrAdd(id).updateItems(entries)
    }

    override fun onName(id: ContainerId, name: ContainerDisplayName) {
        containerStates.getOrAdd(id).apply {
            this.name = name
        }
    }

    override fun onOccupancy(id: ContainerId, occupancy: OccupancyView) {
        containerStates.getOrAdd(id).apply {
            this.occupancy = occupancy
        }
    }

    override fun onWidgetClose(id: ContainerId) {
        inventoryManager.close(id)
    }

    override fun onGroup(group: InventoryGroup) {
        val definitions = group.inventories.map {
            InventoryDefinition(
                containerStates.getOrAdd(it.containerId),
                it.params
            )
        }
        inventoryManager.openGroup(definitions, group.screenPos)
    }
}