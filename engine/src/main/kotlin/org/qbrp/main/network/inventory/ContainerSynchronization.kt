package org.qbrp.main.network.inventory

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.inventory.getInfinityContainerId
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.location.Location
import org.qbrp.main.network.transport.getPlayersInRadius
import org.qbrp.main.network.transport.PacketSendTask

private fun createPacket(
    containerService: ContainerService,
    id: ContainerId,
    items: List<ContainerEntry> = containerService.getItems(id),
): ContainerSyncPacket {
    val name = containerService.getName(id)
    val occupancy = containerService.getOccupancy(id)
    val entries = items.map { ContainerEntryReference(it.stringId) }
    return ContainerSyncPacket(id, entries, name, occupancy)
}

fun sendInventoryGroupPacket(player: PlayerReference, group: InventoryGroup) {
    INVENTORY_GROUP_OPEN_ENDPOINT
        .sendS2C(InventoryGroupOpenPacket(group), player)
}

fun sendInventoryClosePacket(player: PlayerReference, id: ContainerId) {
    INVENTORY_CLOSE_ENDPOINT
        .sendS2C(InventoryClosePacket(id), player)
}

suspend fun CharacterInventory.broadcast(containerService: ContainerService, radius: Double = 16.0) = coroutineScope {
    getContainers().map {
        async { broadcastContainerEntries(it, containerService, radius) }
    }.awaitAll()
}

suspend fun broadcastContainerEntries(
    id: ContainerId,
    containerService: ContainerService,
    radius: Double = 16.0
) = coroutineScope {
    val entity = containerService.getAnchoredEntity(id) ?: return@coroutineScope
    val entityLocation = entity.get<Location>() ?: return@coroutineScope
    val pos = entityLocation.pos
    val world = entityLocation.world

    getPlayersInRadius(pos, world, radius)
        .map { player ->
            async {
                taskSynchronizeContainerEntry(id, player, containerService)
                    .queue()
                    .requestAcknowledge()
            }
        }
        .awaitAll()
}

fun taskSynchronizeContainerEntry(
    id: ContainerId,
    player: PlayerReference,
    containerService: ContainerService,
): PacketSendTask {
    val task = CONTAINER_SYNC_ENDPOINT
        .taskS2C(createPacket(containerService, id), player)
    return task
}

fun synchronizeContainerEntry(
    id: ContainerId,
    player: PlayerReference,
    containerService: ContainerService,
) = taskSynchronizeContainerEntry(id, player, containerService)
    .queue()

suspend fun ServerPlayerObject.synchronizeAllInventories() {
    val containerService: ContainerService = GlobalContext.get().get()
    require<CharacterInventory>().broadcast(containerService)
    synchronizeContainerEntry(getInfinityContainerId(containerService), this, containerService)
}