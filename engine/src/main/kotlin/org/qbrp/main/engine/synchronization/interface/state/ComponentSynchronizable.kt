package org.qbrp.main.engine.synchronization.`interface`.state

import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.synchronization.`interface`.Convertible

interface ComponentSynchronizable: Convertible {
    fun update(cluster: ClusterViewer) {}
}