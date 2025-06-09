package org.qbrp.client.core.synchronization

import org.qbrp.client.engine.inventory.model.AbstractContainerHolder
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.inventory.AbstractContainer
import kotlin.reflect.KClass

class ContainerHolderFabric(val klass: KClass<*>, val toComponentFactory: (ClusterViewer, String) -> AbstractContainerHolder): ComponentFabric {
    override fun toComponent(cluster: ClusterViewer): Component {
        val id = cluster.getEntry(AbstractContainer.SYNC_ID_ENTRY)!!
        return toComponentFactory(cluster, id) as Component
    }

    override fun shouldHandle(name: String): Boolean {
        return klass.simpleName?.contains(name) == true
    }
}