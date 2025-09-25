package org.qbrp.main.engine.inventory.player.impl

import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.inventory.entity.Inventory
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.inventory.entity.ScreenCorner
import org.qbrp.main.engine.inventory.networking.InventoryPacketSender
import org.qbrp.main.engine.inventory.networking.InventorySynchronizer
import org.qbrp.main.engine.inventory.player.PlayerInventoryManager
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext

class ServerPlayerInventoryManager(
    ctx: PlayerComponentInstantiateContext,
    private val packetSender: InventoryPacketSender,
    private val synchronizer: InventorySynchronizer,
    private val containerService: ContainerService
) : PlayerComponent(ctx), PlayerInventoryManager {
    override val openedGroups: MutableList<InventoryGroup> = mutableListOf()
    override val openedInventories: Collection<Inventory> get() = openedGroups.flatMap { it.inventories }
    private val items get() = openedInventories.flatMap { containerService.getItems(it.containerId) }

    override fun getInventory(id: ContainerId): Inventory? {
        return openedInventories.find { it.containerId == id }
    }

    override fun openGroup(group: InventoryGroup) {
        group.inventories.forEach {
            synchronizer.sync(it.containerId, serverPlayer)
            if (isOpened(it.containerId)) return@forEach
        }
        openedGroups.add(group)
        packetSender.sendInventoryGroupPacket(serverPlayer, group)
    }

    override fun open(
        inventory: Inventory,
        corner: ScreenCorner
    ) {
        openGroup(InventoryGroup(listOf(inventory), corner))
    }

    override fun closeGroup(group: InventoryGroup) {
        val removed = openedGroups.remove(group)
        if (!removed) group.inventories.forEach {
            packetSender.sendInventoryClosePacket(serverPlayer, it.containerId)
        }
    }

    override fun <I : ContainerEntry> getItem(id: StringId): I? {
        return items.find { it.stringId == id } as? I
    }

    fun isOpened(container: ContainerId): Boolean {
        return getInventory(container) != null
    }
}