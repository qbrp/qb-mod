package org.qbrp.engine.client.core.events

import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.engine.client.core.visual.VisualDataLoader
import org.qbrp.engine.client.system.ClientModuleManager
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.Messages.LOAD_CHUNK_VISUAL
import org.qbrp.system.networking.messages.Messages.SEND_MESSAGE
import org.qbrp.system.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.system.networking.messages.Messages.UPDATE_VISUAL
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.ClusterListContent
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messages.types.StringContent

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
        ClientReceiver<ClientReceiverContext>(Messages.REGISTRATION_REQUEST, Signal::class) { message, context, receiver ->
            EngineClient.registrationManager.autoLogin()
            true
        }.register()
        ClientReceiver<ClientReceiverContext>(Messages.REGISTRATION_RESPONSE, StringContent::class) { message, context, receiver ->
            ClientResources.root.setAutoLoginCode(message.getContent())
            true
        }.register()
        ClientReceiver<ClientReceiverContext>(SERVER_INFORMATION, Cluster::class) { message, context, receiver ->
            ClientNetworkManager.handleServerInfo(message.getContent())
            true
        }.register()
        //ClientModuleManager.registerStateReceivers()
    }
}