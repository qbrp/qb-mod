package org.qbrp.engine.chat.messages

import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.Component
import org.qbrp.system.networking.messages.components.Cluster

class ChatMessageTagsCluster(components: List<Component> = emptyList()): Cluster(components) {
    override fun getBuilder(): ClusterBuilder {
        return ChatMessageTagsBuilder().apply { components(components!!) }
    }
}