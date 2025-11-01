package org.qbrp.client.core.networking.v1

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.qbrp.main.core.util.networking.messages.Message
import org.qbrp.main.core.util.networking.messaging.Receiver
import org.qbrp.main.core.util.log.LoggerUtil
import kotlin.reflect.KClass

class ClientReceiver(
    messageId: String,
    messageTypeClass: KClass<*>,
    callback: (Message, ClientReceiverContext, Receiver<ClientReceiverContext>) -> Boolean
): Receiver<ClientReceiverContext>(messageId, messageTypeClass, callback) {
    private val logger = LoggerUtil.get("network", "receivers")

    fun register() {
        logger.log("Зарегистрирован ресивер: <<${Identifier("qbrp", messageId)}>>")
        ClientPlayNetworking.registerGlobalReceiver(Identifier("qbrp", messageId)) { client, handler, buf, responseSender ->
            val context = ClientReceiverContext(client, handler)
            try {
                val messageType = createMessageType(buf)
                val message = Message(messageId, messageType)
                handle(message, context)
                callback(message, context, this)
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
