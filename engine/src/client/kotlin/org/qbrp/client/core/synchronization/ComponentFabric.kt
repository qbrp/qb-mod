package org.qbrp.client.core.synchronization

import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

interface ComponentFabric {
    fun toComponent(cluster: ClusterViewer): Component
    fun shouldHandle(name: String): Boolean
}