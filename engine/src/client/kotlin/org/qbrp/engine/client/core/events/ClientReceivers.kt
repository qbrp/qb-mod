package org.qbrp.engine.client.core.events

import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.engine.client.core.visual.VisualDataLoader
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Messages.CHAT_GROUPS
import org.qbrp.system.networking.messages.Messages.LOAD_CHUNK_VISUAL
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messages.Messages.UPDATE_VISUAL
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.ClusterListContent

object ClientReceivers {

    fun register() {
        ClientReceiver<ClientReceiverContext>(LOAD_CHUNK_VISUAL, ClusterListContent::class) { message, context, receiver ->
            VisualDataLoader.loadChunk((message.content as ClusterListContent).list)
            true
        }.register()
        ClientReceiver<ClientReceiverContext>(UPDATE_VISUAL, Cluster::class) { message, context, receiver ->
            val content = message.content as Cluster
            VisualDataLoader.loadContent(content)
            true
        }.register()
        ClientReceiver<ClientReceiverContext>(CHAT_GROUPS, ClusterListContent::class) { message, context, receiver ->
            EngineClient.getChatModuleAPI()?.loadChatGroups(
                message.getContent<List<Cluster>>().map { cluster ->
                    EngineClient.getChatModuleAPI()?.createChatGroupFromCluster(cluster.getData())
                }.filterNotNull()
            )
            true
        }.register()
        ClientReceiver<ClientReceiverContext>(SEND_MESSAGE, Cluster::class) { message, context, receiver ->
            EngineClient.getChatModuleAPI()?.handleMessageFromServer(message)
            true
        }.register()

    }
}