package org.qbrp.client.core.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.types.SendContent
import org.qbrp.main.core.utils.networking.messages.types.Signal
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.core.utils.networking.messaging.NetworkMessageSender
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

object ClientNetworkUtil: NetworkMessageSender {
    private val logger = LoggerUtil.get("network", "sending")

    override fun sendMessage(
        message: Message,
        target: ServerPlayerEntity?
    ) {
        val content = message.content as SendContent
        val data = content.write()
        ClientPlayNetworking.send(message.minecraftIdentifier, data)
        logger.log("CLIENT --> <<${message.identifier}>>")
    }

    fun sendSignal(name: String) {
        sendMessage(Message(name, Signal()))
    }

    fun <T : Any> responseRequest(
        message: Message,
        responseClass: KClass<T>
    ): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        val receiver = ClientReceiver(message.identifier, responseClass) { responseMessage, context, receiver ->
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
