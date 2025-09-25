package org.qbrp.main.engine.inventory.networking.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.networking.getPlayersInRadius
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.inventory.InventoriesModule
import org.qbrp.main.engine.inventory.networking.InventorySynchronizer
import org.qbrp.main.engine.inventory.packet.ContainerSyncPacket
import org.qbrp.main.engine.items.networking.ItemSynchronizer
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.location.Location

class ContainerSynchronizerImpl(
    private val packetSender: ServerPacketSender,
    private val containerService: ContainerService
): InventorySynchronizer {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun createPacket(
        id: ContainerId,
        items: List<ContainerEntry> = containerService.getItems(id)
    ): ContainerSyncPacket {
        val name = containerService.getName(id)
        val occupancy = containerService.getOccupancy(id)
        return ContainerSyncPacket(id, items, name, occupancy)
    }

    override fun broadcast(id: ContainerId) {
        coroutineScope.launch {
            val entity = containerService.getAnchoredEntity(id) ?: return@launch
            val entityLocation = entity.get<Location>() ?: return@launch
            val items = containerService.getItems(id)
            val pos = entityLocation.pos
            val world = entityLocation.world

            getPlayersInRadius(pos, world, SYNC_RADIUS)
                .map { player ->
                    async {
                        supervisorScope {
                            items.mapNotNull { it.get<ItemSynchronizer>() }
                                .map { sync -> async { sync.getBatchTask(player).queue().waitAcknowledge() } }
                                .awaitAll()
                        }
                        packetSender.sendImmediately(createPacket(id, items), InventoriesModule.ITEMS, player)
                    }
                }
                .awaitAll()
        }
    }

    override fun sync(
        id: ContainerId,
        player: ServerPlayerObject
    ) {
        packetSender.sendImmediately(
            createPacket(id),
            InventoriesModule.ITEMS,
            player
        )
    }

    companion object {
        const val SYNC_RADIUS = 5.0
    }
}