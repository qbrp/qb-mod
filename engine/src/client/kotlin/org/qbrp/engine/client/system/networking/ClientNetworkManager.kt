package org.qbrp.engine.client.system.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.qbrp.core.keybinds.ServerKeyBind
import org.qbrp.engine.Engine
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.keybinds.KeybindsManager
import org.qbrp.engine.client.engine.chat.ClientChatAPI
import org.qbrp.engine.client.engine.chat.addons.ClientChatGroupsAPI
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.ServerInformation
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer
import org.qbrp.system.networking.messages.types.ClusterListContent
import org.qbrp.system.networking.messages.types.SendContent
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.utils.log.Loggers
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

object ClientNetworkManager {
    private val logger = Loggers.get("network", "sending")
    var serverInformation: ServerInformation? = null

    fun sendMessage(message: Message) {
        val content = message.content as SendContent
        val data = content.write()
        ClientPlayNetworking.send(message.minecraftIdentifier, data)
        logger.log("CLIENT --> <<${message.identifier}>>")
    }

    fun sendSignal(name: String) {
        sendMessage(Message(name, Signal()))
    }

    fun handleServerInfo(cluster: ClusterViewer) {
        val groupsApi = EngineClient.getAPI<ClientChatGroupsAPI>()
        cluster.getComponentData<List<Cluster>>("engine.chatGroups")?.let {
            groupsApi?.loadChatGroups(
                it.map { cluster ->
                    groupsApi.createChatGroupFromCluster(cluster.getData())
                }
            )
        }

        val serverKeybinds =  cluster.getComponentData<List<Cluster>>("core.keybinds")
            ?.map {
                val data = it.getData()
                ServerKeyBind(
                    data.getComponentData<String>("id")!!,
                    data.getComponentData<Int>("defaultKey")!!,
                    data.getComponentData<String>("name")!!
                )
            }
        ?: emptyList()
        ServerInformation.VIEWER = cluster

        val keybindsManager = EngineClient.keybindsManager
        serverKeybinds.forEach {
            if (!keybindsManager.keybindExists(it.id)) {
                val keybind = keybindsManager.createKeybinding(it.id, it.defaultKey)
                keybindsManager.registerHiddenKeyBinding(keybind, it.id) { }
            }
        }
    }

    fun <T : Any> responseRequest(
        message: Message,
        responseClass: KClass<T>
    ): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        val receiver = ClientReceiver<ClientReceiverContext>(message.identifier, responseClass) { responseMessage, context, receiver ->
            ClientPlayNetworking.unregisterReceiver(Identifier(message.identifier))
            if (responseMessage.identifier == message.identifier) {
                @Suppress("UNCHECKED_CAST")
                val content = responseMessage.content as T
                future.complete(content)
            } else {
                future.completeExceptionally(IllegalStateException("Invalid response identifier"))
            }
        }
        receiver.register()
        sendMessage(message)
        return future
    }

}
