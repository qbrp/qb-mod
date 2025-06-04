package org.qbrp.client.core.synchronization

import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.synchronization.`interface`.state.ComponentSynchronizable

class ObjectChannelReceiver<T: BaseObject>(val name: String,
                                             val storage: Storage<T>,
                                             val converter: (ClusterViewer, String) -> T) {
    val fabrics: MutableList<ComponentFabric> = mutableListOf()
    val shouldOverride: (ClusterViewer) -> Boolean = { false }

    fun run() {
        ClientReceiver<ClientReceiverContext>(Messages.syncChannel(name), Cluster::class) { message, context, receiver ->
            val cluster: ClusterViewer = message.getContent()
            val id = cluster.getComponentData<String>("id")!!
            val state = cluster.getCluster<Cluster>("state")!!.getData()
            val componentNames = cluster.getComponentData<List<String>>("components")!!

            val obj = handleObject(id, cluster)
            componentNames.forEach { handleComponent(it, state.getCluster<Cluster>(it)!!.getData(), obj) }
            true
        }.register()
    }

    fun addFabric(fabric: ComponentFabric): ObjectChannelReceiver<T> {
        fabrics.add(fabric)
        return this
    }

    private fun handleObject(id: String, cluster: ClusterViewer): T {
        if (shouldOverride(cluster)) {
            storage.remove(id)
        }
        val existing = storage.getByKey(id)
        if (existing != null) {
            return existing
        }
        val newInstance = converter(cluster, id)
        storage.add(newInstance)
        return newInstance
    }

    private fun handleComponent(componentType: String, cluster: ClusterViewer, obj: T) {
        val fabric = getFabric(componentType)
        val component = obj.state.getComponentByName(componentType)
        if (component != null) {
            (component as ComponentSynchronizable).update(cluster)
        } else {
            obj.state.addComponent(fabric.toComponent(cluster))
        }
    }

    private fun getFabric(type: String): ComponentFabric {
        val qualifiedName = type.split("#").first()
        return fabrics.find { it.shouldHandle(qualifiedName)}!!
    }
}