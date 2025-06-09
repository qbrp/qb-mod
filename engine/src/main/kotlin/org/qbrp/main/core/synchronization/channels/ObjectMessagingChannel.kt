package org.qbrp.main.core.synchronization.channels

import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.storage.ObjectProvider
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

class ObjectMessagingChannel<T: BaseObject>(channelName: String, provider: ObjectProvider<T>
): ObjectProviderChannel<T>(channelName, FetchOnlyResolver<T>(provider)) {

    override fun getId(viewer: ClusterViewer): String {
        return viewer.getComponentData("id")!!
    }

    override fun onFound(obj: T, viewer: ClusterViewer) {
        val messageId: String = viewer.getComponentData("messaging.id")!!
        val content: ClusterViewer = viewer.getComponentData("messaging.content")!!
        obj.state.onMessage(messageId, content)
    }

    override fun onFound(obj: T, viewer: ClusterViewer, playerObject: ServerPlayerObject) {
        val messageId: String = viewer.getComponentData("messaging.id")!!
        val content: ClusterViewer = viewer.getComponentData("messaging.content")!!
        obj.state.onMessage(messageId, playerObject, content)
    }
}
