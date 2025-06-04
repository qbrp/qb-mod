package org.qbrp.client.core.synchronization

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.Component
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.synchronization.`interface`.state.SynchronizeUpdate

class SyncResolver<T: BaseObject>(
    private val storage: Storage<T>,
    private val factory: ObjectFactory<T>
) : ObjectResolver<T> {

    private val fabrics: MutableList<ComponentFabric> = mutableListOf()
    private val associations: MutableMap<String, String> = mutableMapOf()
    var shouldOverride: (T, ClusterViewer) -> Boolean = { _, _ -> false }

    fun addFabric(fabric: ComponentFabric) {
        fabrics.add(fabric)
    }

    fun addAssociation(serverComponent: String, clientComponent: String) {
        associations[serverComponent] = clientComponent
    }

    override fun resolve(viewer: ClusterViewer, id: String): T {
        val obj = storage.getById(id)
        if (obj != null) {
            if (shouldOverride(obj, viewer)) {
                storage.remove(obj.id)
                val recreated = factory.create(viewer, id)
                storage.add(recreated)
                return updateComponents(recreated, viewer)
            } else {
                return updateComponents(obj, viewer)
            }
        } else {
            val obj =storage.add(factory.create(viewer, id))
            return updateComponents(obj, viewer)
        }
    }

    private fun updateComponents(obj: T, viewer: ClusterViewer): T {
        val stateCluster: ClusterViewer = viewer.getCluster<Cluster>("state")!!.getData()
        val componentNames: List<String> = viewer.getComponentData<List<String>>("components")!!

        for (compName in componentNames) {
            val qualified = compName.split("#").first()
            fabrics.find { it.shouldHandle(qualified) }?.let {
                val compCluster: ClusterViewer = stateCluster
                    .getCluster<Cluster>(compName)!!
                    .getData()
                val associatedName = associations[compName] ?: compName
                val existingComp = obj.state.getComponentByName(associatedName)
                if (existingComp != null) {
                    (existingComp as? SynchronizeUpdate)?.update(compCluster)
                } else {
                    val newComp = it.toComponent(compCluster)
                    obj.state.addComponent(newComp)
                }
            }
        }
        return obj
    }
}