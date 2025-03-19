package org.qbrp.core.keybinds

import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.components.ClusterBuilder


data class ServerKeyBind(val id: String, val defaultKey: Int, val name: String) {
    fun toCluster(): Cluster {
        return ClusterBuilder()
            .component("id", id)
            .component("defaultKey", defaultKey)
            .component("name", name)
            .build()
    }
}