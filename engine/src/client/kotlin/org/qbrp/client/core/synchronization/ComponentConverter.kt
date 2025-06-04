package org.qbrp.client.core.synchronization

import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import kotlin.reflect.KClass

class ComponentConverter(val klass: KClass<*>, val toComponentFactory: (ClusterViewer) -> Component): ComponentFabric {
    override fun toComponent(cluster: ClusterViewer): Component {
        return toComponentFactory(cluster)
    }

    override fun shouldHandle(name: String): Boolean {
        return klass.simpleName?.contains(name) == true
    }
}