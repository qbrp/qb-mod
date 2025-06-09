package org.qbrp.main.core.synchronization.channels

import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.core.synchronization.state.SynchronizeUpdate

abstract class ObjectProviderChannel<T: Identifiable>(
    val channelName: String,
    protected open val resolver: ObjectResolver<T>
) {
    abstract fun getId(viewer: ClusterViewer): String
    open fun onFound(obj: T, viewer: ClusterViewer) {}
    open fun onFound(obj: T, viewer: ClusterViewer, playerObject: ServerPlayerObject) {}
    open fun onError(viewer: ClusterViewer, id: String, err: Exception?) {
        err?.printStackTrace()
    }
    fun handleCluster(cluster: ClusterViewer, playerObject: ServerPlayerObject? = null) {
        val id = getId(cluster)
        try {
            val obj = resolver.resolve(cluster, id)
            if (obj is SynchronizeUpdate) obj.update(cluster)
            if (obj != null) {
                if (playerObject == null) onFound(obj, cluster)
                else onFound(obj, cluster, playerObject)
            }
        } catch (e: Exception) {
            onError(cluster, id, e)
        }
        true
    }

    fun run() {
        ServerReceiver<ServerReceiverContext>(
            Messages.syncChannel(channelName),
            Cluster::class,
            { message, context, receiver ->
            handleCluster(message.getContent(), PlayersUtil.getPlayerSession(context.player))
            true
        }).register()
    }
}
