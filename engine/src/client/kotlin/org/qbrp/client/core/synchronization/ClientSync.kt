package org.qbrp.client.core.synchronization

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.core.synchronization.channels.ObjectProviderChannel

fun <T: Identifiable> ObjectProviderChannel<T>.runClient() {
    ClientReceiver(
        Messages.syncChannel(channelName),
        Cluster::class,
        { message, context, receiver ->
            handleCluster(message.getContent(), null)
            true
        }).register()
}

fun <T: Identifiable> Storage<T>.enableClear() {
    ClientPlayConnectionEvents.DISCONNECT.register { handler, client ->
        this.clear()
    }
}