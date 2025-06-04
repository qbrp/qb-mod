package org.qbrp.main.engine.synchronization.`interface`

import org.qbrp.main.core.utils.networking.messages.components.Cluster

interface Convertible {
    fun toCluster(): Cluster
}