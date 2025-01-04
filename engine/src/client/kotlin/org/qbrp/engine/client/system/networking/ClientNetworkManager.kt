package org.qbrp.engine.client.system.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.Message
import org.qbrp.system.utils.log.Loggers
import kotlin.reflect.KClass

object ClientNetworkManager {
    private val logger = Loggers.get("network", "sending")

    fun sendMessage(message: Message) {
        val data = message.content.writeByteBuf()
        ClientPlayNetworking.send(message.minecraftIdentifier, data)
        logger.log("CLIENT --> <<${message.identifier}>> (${message.content})")
    }

    fun responseRequest(
        message: Message,
        responseClass: KClass<*>,
        callback: (Message?) -> Unit
    ) {
        val receiver = ClientReceiver(message.identifier, responseClass) { responseMessage, _ ->
            ClientPlayNetworking.unregisterReceiver(Identifier(message.identifier))
            if (responseMessage.identifier == message.identifier) callback(responseMessage)
        }
        receiver.register()
        sendMessage(message)
    }
}
