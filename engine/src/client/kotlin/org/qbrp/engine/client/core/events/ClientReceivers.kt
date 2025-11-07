package org.qbrp.engine.client.core.events

import net.minecraft.client.MinecraftClient
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.engine.client.system.ClientModuleManager
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer
import org.qbrp.system.networking.messages.types.ClusterListContent
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.utils.format.Format.asMiniMessage

fun registerClientReceivers(client: MinecraftClient) {
    ClientReceiver<ClientReceiverContext>(Messages.REGISTRATION_REQUEST, Signal::class) { message, context, receiver ->
        EngineClient.registrationManager.autoLogin()
        true
    }.register()
    ClientReceiver<ClientReceiverContext>(Messages.REGISTRATION_RESPONSE, StringContent::class) { message, context, receiver ->
        ClientResources.root.setAutoLoginCode(message.getContent())
        true
    }.register()
    ClientReceiver<ClientReceiverContext>(Messages.NICKNAME_SHOW, Cluster::class) { message, context, receiver ->
        val cluster = message.getContent<ClusterViewer>()
        println("Received name data packet")
        val name = cluster.getComponentData<String>("name") ?: return@ClientReceiver true
        val rpName = cluster.getComponentData<String>("rp") ?: return@ClientReceiver true
        val template = cluster.getComponentData<String>("template") ?: return@ClientReceiver true
        val message = template
            .replace("{playerName}", name)
            .replace("{playerDisplayName}", rpName)
            .asMiniMessage()
        println("Packet composed")
        client.player?.sendMessage(message, true)
        println("Packet visualise")
        true
    }.register()
    ClientReceiver<ClientReceiverContext>(SERVER_INFORMATION, Cluster::class) { message, context, receiver ->
        ClientNetworkManager.handleServerInfo(message.getContent())
        true
    }.register()
}