package org.qbrp.client.core.synchronization

import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

abstract class ObjectProviderChannel<T: Identifiable>(
    private val channelName: String,
    protected open val resolver: ObjectResolver<T>
) {
    abstract fun getId(viewer: ClusterViewer): String
    open fun onFound(obj: T, viewer: ClusterViewer) {}
    open fun onError(viewer: ClusterViewer, id: String, err: Exception?) {
        err?.printStackTrace()
    }

    fun run() {
        ClientReceiver<ClientReceiverContext>(
            Messages.syncChannel(channelName),
            Cluster::class
        ) { message, context, receiver ->
            val viewer = message.getContent<ClusterViewer>()
            val id = getId(viewer)

            try {
                val obj = resolver.resolve(viewer, id)
                onFound(obj, viewer)
            } catch (e: Exception) {
                onError(viewer, id, e)
            }
            true
        }.register()
    }
}
