package org.qbrp.main.engine.synchronization.`interface`.state

import org.qbrp.main.core.game.model.Stateful
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.anticheat.StringListContent
import org.qbrp.main.engine.synchronization.`interface`.Convertible
import org.qbrp.main.engine.synchronization.`interface`.Synchronizable
import org.qbrp.main.engine.synchronization.`interface`.Synchronizer

interface ObjectSynchronizable: Synchronizable, Convertible, Stateful, Identifiable {
    override fun toCluster(): Cluster {
        val cluster = ClusterBuilder.concat(ClusterBuilder(), getCluster().getBuilder())
        cluster.component("id", id)

        val componentsCluster = ClusterBuilder()
        state.components.forEach { (k, v) ->
            if (v is Convertible) componentsCluster.component(k, v.toCluster())
        }
        cluster.component("state", componentsCluster.build())

        val componentsListCluster = StringListContent().apply { list = state.components.keys.toList() }
        cluster.component("components", componentsListCluster)
        return cluster.build()
    }

    override fun synchronize(playerObject: PlayerObject, synchronizer: Synchronizer) {
        synchronizer.sendMessage(playerObject, toCluster())
    }
    fun getCluster(): Cluster = Cluster()
}