package org.qbrp.main.core.utils.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messaging.Receiver
import org.qbrp.main.core.utils.networking.messaging.ReceiverContext
import org.qbrp.main.core.utils.log.LoggerUtil
import kotlin.reflect.KClass

class ClientReceiver<T : ReceiverContext>(
    messageId: String,
    messageTypeClass: KClass<*>,
    callback: (Message, T, Receiver<T>) -> Boolean
): Receiver<T>(messageId, messageTypeClass, callback) {
    private val logger = LoggerUtil.get("network", "receiving")

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
