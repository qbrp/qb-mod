package org.qbrp.system.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.util.Identifier
import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messaging.Receiver
import org.qbrp.system.networking.messaging.ReceiverContext
import org.qbrp.system.utils.log.Loggers
import kotlin.reflect.KClass

class ClientReceiver<T : ReceiverContext>(
    messageId: String,
    messageTypeClass: KClass<*>,
    callback: (Message, T, Receiver<T>) -> Boolean
): Receiver<T>(messageId, messageTypeClass, callback) {
    private val logger = Loggers.get("network", "receiving")

    fun register() {
        logger.log("Зарегистрирован ресивер: <<${Identifier("qbrp", messageId)}>>")
        ClientPlayNetworking.registerGlobalReceiver(Identifier("qbrp", messageId)) { client, handler, buf, responseSender ->
            val context = ClientReceiverContext(client, handler)
            try {
                val messageType = createMessageType(buf)
                val message = Message(messageId, messageType)
                handle(message, context)
                callback(message, context as T, this)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("Ошибка обработки пакета $messageId: ${e.message}")
            }
        }
    }

    private fun handle(message: Message, context: ClientReceiverContext) {
        logger.log("${context.client.player?.name?.string} --> <<${message.identifier}>> (${message.content})")
    }
}
