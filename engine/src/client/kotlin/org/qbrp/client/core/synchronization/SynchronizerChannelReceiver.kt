package org.qbrp.client.core.synchronization

import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

class SynchronizerChannelReceiver<T: Identifiable>(val name: String, val storage: Storage<T>, val converter: (ClusterViewer) -> T) {
    init {
        ClientReceiver<ClientReceiverContext>(Messages.syncChannel(name), Cluster::class) { message, context, receiver ->
            val obj = converter(message.getContent())
            storage.remove(obj.id)
            storage.add(obj)
            true
        }.register()
    }
}