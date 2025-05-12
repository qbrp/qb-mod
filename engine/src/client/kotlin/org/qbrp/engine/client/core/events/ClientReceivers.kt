package org.qbrp.engine.client.core.events

import net.minecraft.client.MinecraftClient
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.ClusterListContent
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messages.types.StringContent

object ClientReceivers {

    fun register() {
        ClientReceiver<ClientReceiverContext>(Messages.AUTH, StringContent::class) { message, context, receiver ->
            ClientAuthEvent.EVENT.invoker().onAuth(context.handler)
            true
        }.register()
        ClientReceiver<ClientReceiverContext>("registration_request", Signal::class) { message, context, receiver ->
            EngineClient.registrationManager.autoLogin()
            true
        }.register()
        ClientReceiver<ClientReceiverContext>(SERVER_INFORMATION, Cluster::class) { message, context, receiver ->
            ClientNetworkManager.handleServerInfo(message.getContent())
            true
        }.register()
        ClientReceiver<ClientReceiverContext>(Messages.INVOKE_COMMAND, StringContent::class) { message, context, receiver ->
            MinecraftClient.getInstance().player?.networkHandler?.sendChatCommand(message.getContent())
            true
        }.register()
        EngineClient.moduleManager.registerStateReceivers()
    }
}