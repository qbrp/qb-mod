package org.qbrp.client.core.synchronization

import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.ObjectProvider
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.types.ReceiveContent

class LocalObjectChannel<T: BaseObject>(channelName: String, provider: ObjectProvider<T>
): ObjectProviderChannel<T>(channelName, FetchOnlyResolver<T>(provider)) {

    override fun getId(viewer: ClusterViewer): String {
        return viewer.getComponentData("id")!!
    }

    override fun onFound(obj: T, viewer: ClusterViewer) {
        val messageId: String = viewer.getComponentData("messaging.id")!!
        val content: ClusterViewer = viewer.getComponentData("messaging.content")!!
        obj.state.broadcastMessage(messageId, content)
    }
}
