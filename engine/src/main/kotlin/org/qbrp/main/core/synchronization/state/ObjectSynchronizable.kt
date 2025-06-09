package org.qbrp.main.core.synchronization.state

import org.qbrp.main.core.game.model.Stateful
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.anticheat.StringListContent
import org.qbrp.main.core.synchronization.Synchronizable
import org.qbrp.main.core.synchronization.SynchronizeConvertible
import org.qbrp.main.core.synchronization.Synchronizer

interface ObjectSynchronizable: Synchronizable, SynchronizeConvertible, Stateful, Identifiable {
    override fun toCluster(playerObject: ServerPlayerObject): Cluster {
        val cluster = ClusterBuilder.concat(ClusterBuilder(), getCluster().getBuilder())
        cluster.component("id", id)

        val componentsCluster = ClusterBuilder()
        state.componentsMap.forEach { (k, v) ->
            if (v is SynchronizeConvertible) componentsCluster.component(k, v.toCluster(playerObject))
        }
        cluster.component("state", componentsCluster.build())

        val componentsListCluster = StringListContent().apply { list = state.componentsMap.keys.toList() }
        cluster.component("components", componentsListCluster)
        return cluster.build()
    }
    override fun synchronize(playerObject: ServerPlayerObject, synchronizer: Synchronizer) {
        synchronizer.sendMessage(playerObject, toCluster(playerObject))
    }
    fun getCluster(): Cluster = Cluster()
}