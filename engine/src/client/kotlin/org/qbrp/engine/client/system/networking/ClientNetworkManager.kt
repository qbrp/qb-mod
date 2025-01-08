package org.qbrp.engine.client.system.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.utils.log.Loggers
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

object ClientNetworkManager {
    private val logger = Loggers.get("network", "sending")

    fun sendMessage(message: Message) {
        val data = message.content.writeByteBuf()
        ClientPlayNetworking.send(message.minecraftIdentifier, data)
        logger.log("CLIENT --> <<${message.identifier}>> (${message.content})")
    }

    fun <T : Any> responseRequest(
        message: Message,
        responseClass: KClass<T>
    ): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        val receiver = ClientReceiver(message.identifier, responseClass) { responseMessage, context ->
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
