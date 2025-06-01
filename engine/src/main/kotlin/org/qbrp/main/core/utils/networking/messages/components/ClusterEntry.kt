package org.qbrp.main.core.utils.networking.messages.components

import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.types.SendContent

class ClusterEntry <T> (val name: String) {
    fun read(viewer: ClusterViewer) = viewer.getComponentData<T>(name)
    fun write(builder: ClusterBuilder, value: SendContent) = builder.component(name, value)
    fun write(builder: ClusterBuilder, value: String) = builder.component(name, value)
}