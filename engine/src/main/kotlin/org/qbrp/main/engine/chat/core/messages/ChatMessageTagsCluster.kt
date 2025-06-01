package org.qbrp.main.engine.chat.core.messages

import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.Component
import org.qbrp.main.core.utils.networking.messages.components.Cluster

class ChatMessageTagsCluster(components: List<Component> = emptyList()): Cluster(components) {
    override fun getBuilder(): ClusterBuilder {
        return ChatMessageTagsBuilder().apply { components(components!!) }
    }

    override fun setData(data: Any) {
        throw UnsupportedOperationException()
    }
}