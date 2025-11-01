package org.qbrp.client.network.play

import org.koin.core.component.KoinComponent
import org.qbrp.client.engine.inventory.model.InventoryDefinition
import org.qbrp.client.engine.inventory.service.ContainerStates
import org.qbrp.client.engine.inventory.service.InventoryManager
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.client.network.transport.registerClientReceiver
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.network.inventory.CONTAINER_SYNC_ENDPOINT
import org.qbrp.main.network.inventory.ContainerEntryReference
import org.qbrp.main.network.inventory.INVENTORY_CLOSE_ENDPOINT
import org.qbrp.main.network.inventory.INVENTORY_GROUP_OPEN_ENDPOINT
import kotlin.concurrent.thread

fun KoinComponent.runClientInventoryHandler(
    inventoryManager: InventoryManager,
    containerStates: ContainerStates,
    itemStorage: ClientItemStorage
) {
    val pendingSyncs = mutableListOf<PendingContainerSync>()

    // Фоновая проверка отложенных контейнеров
    thread(name = "Inventory Pending Sync Handler", isDaemon = true) {
        while (true) {
            if (pendingSyncs.isNotEmpty()) {
                val iterator = pendingSyncs.iterator()
                while (iterator.hasNext()) {
                    val pending = iterator.next()
                    val resolvedItems = pending.entries.mapNotNull { itemStorage.getById(it.id) }

                    if (resolvedItems.size == pending.entries.size) {
                        containerStates.getOrAdd(pending.containerId).updateItems(resolvedItems)
                        iterator.remove()
                    } else {
                        pending.retries++
                        if (pending.retries > 100) {
                            iterator.remove()
                        }
                    }
                }
            }

            Thread.sleep(10)
        }
    }

    INVENTORY_GROUP_OPEN_ENDPOINT
        .registerClientReceiver {
            val definitions = group.inventories.map {
                InventoryDefinition(
                    containerStates.getOrAdd(it.containerId),
                    it.params
                )
            }
            inventoryManager.openGroup(definitions, group.screenPos)
        }

    INVENTORY_CLOSE_ENDPOINT
        .registerClientReceiver {
            inventoryManager.close(id)
        }

    CONTAINER_SYNC_ENDPOINT
        .registerClientReceiver {
            val items = entries.mapNotNull { itemStorage.getById(it.id) }
            val container = containerStates.getOrAdd(containerId)

            if (items.size < entries.size) {
                pendingSyncs.add(PendingContainerSync(containerId, entries))
            } else {
                container.updateItems(items)
            }

            container.occupancy = occupancy
            container.name = name
        }
}

private data class PendingContainerSync(
    val containerId: ContainerId,
    val entries: List<ContainerEntryReference>,
    var retries: Int = 0
)
