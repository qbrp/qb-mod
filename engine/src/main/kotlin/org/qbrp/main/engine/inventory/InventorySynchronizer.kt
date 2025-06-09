package org.qbrp.main.engine.inventory

import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.types.ClusterListContent
import org.qbrp.main.core.synchronization.SynchronizeConvertible
import org.qbrp.main.core.synchronization.components.InternalMessageReceiver
import org.qbrp.main.core.synchronization.components.S2CMessaging

class InventorySynchronizer(
    val channel: S2CMessaging
): Component(), SynchronizeConvertible, InventoryHandler {
    private fun getContainer(id: String): Container {
        return requireState()
            .getComponentsIsInstance<Container>()
            .firstOrNull { it.id == id } ?: throw IllegalStateException()
    }

    override fun handleTakeItem(inventoryId: String, slot: Int, player: ServerPlayerObject): InventoryEntry? {
        val container = getContainer(inventoryId)
        val item = container.takeItem(slot)
        return item
            .also { sync(player, container) }
    }

    override fun handlePutItem(inventoryId: String, item: InventoryEntry, slot: Int, player: ServerPlayerObject): InventoryEntry? {
        val container = getContainer(inventoryId)
        return container.putItem(slot, item)
            .also { sync(player, container) }
    }

    override fun handleSwapItem(inventoryId: String, item: InventoryEntry, slot: Int, player: ServerPlayerObject): InventoryEntry {
        val container = getContainer(inventoryId)
        return container.swapItem(slot, item)
            .also { sync(player, container) }
    }

    private fun sync(player: ServerPlayerObject, container: Container) {
        channel.sendMessage(toClusterSync(player, container), player, "inventory.sync")
    }

    // Частичная синхронизация - данные только измененного контейнера
    private fun toClusterSync(player: ServerPlayerObject, container: Container): Cluster {
        return toCluster(player, listOf(container))
    }

    override fun toCluster(player: ServerPlayerObject): Cluster {
        return toCluster(player, requireState().getComponentsIsInstance<Container>())
    }

    private fun toCluster(player: ServerPlayerObject, containers: List<Container>): Cluster {
        val builder = ClusterBuilder()
        val clusteredContainers =
            containers.map { container -> ClusterBuilder()
                .component("entries", ClusterListContent()
                    .apply { list = container.getEntries().map { it.toCluster(player) } }
                )
                .component("id", container.id)
                .build()
            }
        builder.component("containers", ClusterListContent().apply { list = clusteredContainers })
        return builder.build()
    }

    private fun onError(exception: Exception, player: ServerPlayerObject) {
        exception.printStackTrace()
        channel.sendMessage(
            ClusterBuilder()
                .component("text", exception.message!!)
                .build(),
            player,
            "inventory.error"
        )
    }
}