package org.qbrp.main.network.inventory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.events.on
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.Container
import org.qbrp.main.engine.containers.event.ContainerCloseIntentionEvent
import org.qbrp.main.engine.containers.event.ContainerOpenEvent
import org.qbrp.main.engine.containers.event.ItemAddedInContainerEvent
import org.qbrp.main.engine.containers.event.ItemRemovedFromContainerEvent
import org.qbrp.main.engine.containers.system.TransferSystem
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.players.model.ServerPlayerObject

data class Inventory(val containerId: ContainerId, val params: InventoryParams = InventoryParams())

data class InventoryGroup(val inventories: List<Inventory>, val screenPos: ScreenCorner)

data class InventoryParams(val maxRows: Int? = null, val maxColumns: Int = 7) {
    companion object {
        val DEFAULT = InventoryParams(null)
        val HORIZONTAL = InventoryParams(1)
    }
}

enum class ScreenCorner {
    TOP_LEFT, BOTTOM_LEFT
}

data class ServerInventories(
    val states: MutableMap<ServerPlayerObject, PlayerInventoryGroups> = mutableMapOf()
) {
    fun get(player: ServerPlayerObject): PlayerInventoryGroups {
        return states.getOrPut(player) { PlayerInventoryGroups(player, mutableListOf()) }
    }
}

data class PlayerInventoryGroups(
    val player: PlayerReference,
    val openedGroups: MutableList<InventoryGroup> = mutableListOf()
) {
    val openedInventories: Collection<Inventory> get() = openedGroups.flatMap { it.inventories }

    fun getInventory(id: ContainerId): Inventory? {
        return openedInventories.find { it.containerId == id }
    }

    fun isOpened(container: ContainerId): Boolean {
        return getInventory(container) != null
    }
}


fun KoinComponent.runServerInventoriesHandler(
    inventories: ServerInventories,
    containerService: ContainerService = get()
) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    on<ContainerOpenEvent>() {
        inventories.get(by).openInventory(
            Inventory(id),
            ScreenCorner.TOP_LEFT,
            containerService
        )
    }

    on<ContainerCloseIntentionEvent>() {
        inventories.get(by).apply {
            openedGroups.forEach {
                if (it.inventories.any { it.containerId == id}) {
                    closeGroup(it)
                }
            }
        }
    }

    on<ItemAddedInContainerEvent> {
        // При удалении предмета закрываем все его открытые сессии
        if (containerId == TransferSystem.TRASH_CAN) {
            val innerContainers = entry
                .filterIsInstance<Container>()
                .map { it.id }

            inventories.states.forEach { player, groups ->
                groups.openedGroups
                    .filter { it.inIds(innerContainers) }
                    .forEach { groups.closeGroup(it) }

            }
        }

        coroutineScope.launch { broadcastContainerEntries(containerId, containerService) }
    }
    on<ItemRemovedFromContainerEvent> {
        coroutineScope.launch { broadcastContainerEntries(containerId, containerService) }
    }
}

fun PlayerInventoryGroups.openGroup(group: InventoryGroup, containerService: ContainerService) {
    group.inventories.forEach {
        synchronizeContainerEntry(it.containerId, player, containerService)
        if (isOpened(it.containerId)) return@forEach
    }
    openedGroups.add(group)
    sendInventoryGroupPacket(player, group)
}

fun PlayerInventoryGroups.openInventory(
    inventory: Inventory,
    corner: ScreenCorner,
    containerService: ContainerService
) {
    openGroup(InventoryGroup(listOf(inventory), corner), containerService)
}

fun PlayerInventoryGroups.closeGroup(group: InventoryGroup) {
    val removed = openedGroups.remove(group)
    if (!removed) group.inventories.forEach {
        sendInventoryClosePacket(player, it.containerId)
    }
}

fun Inventory.inIds(ids: List<ContainerId>) = ids.any { it == containerId }

fun InventoryGroup.inIds(ids: List<ContainerId>) = inventories.any { it.inIds(ids) }