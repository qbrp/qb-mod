package org.qbrp.client.engine.inventory.model

import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.items.model.ItemStorage
import org.qbrp.main.core.synchronization.components.InternalMessageReceiver
import org.qbrp.main.core.synchronization.state.SynchronizeUpdate

class InventoryListener(val storage: ItemStorage<ClientItemObject>): Component(), SynchronizeUpdate, InternalMessageReceiver {
    private fun getContainer(id: String): ItemHolder {
        return requireState()
            .getComponentsIsInstance<ItemHolder>()
            .firstOrNull { it.id == id } ?: throw IllegalStateException()
    }

    override fun update(cluster: ClusterViewer) {
        val containers = cluster.getComponentData<List<Cluster>>("containers")!!.map { it.getData() }
        containers.forEach { containerCluster ->
            val id = containerCluster.getComponentData<String>("id")!!
            val container = getContainer(id)
            val entriesIds = containerCluster.getComponentData<List<Cluster>>("entries")!!
                .map { it.getData().getComponentData<String>("id")!! }
            val items = entriesIds
                .mapNotNull { storage.getById(it) }
            container.setItems(items)
        }
    }

    override fun onMessage(id: String, cluster: ClusterViewer) {
        if (id == "inventory.sync") update(cluster)
    }
}