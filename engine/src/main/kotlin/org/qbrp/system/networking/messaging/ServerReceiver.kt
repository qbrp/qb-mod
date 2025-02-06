package org.qbrp.system.networking.messaging

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.types.BilateralContent
import org.qbrp.system.networking.messages.types.SendContent
import org.qbrp.system.utils.log.Loggers
import kotlin.reflect.KClass

class ServerReceiver<T : ReceiverContext>(
    messageId: String,
    messageTypeClass: KClass<*>,
    callback: (Message, T, Receiver<T>) -> Boolean,
    val callbackOnError: ((Message, T, Receiver<T>) -> Unit)? = null // Лямбда с дефолтным значением
) : Receiver<T>(messageId, messageTypeClass, callback) {
    private val logger = Loggers.get("serverNetwork", "receiving")

    fun register() {
        logger.log("Зарегистрирован ресивер: <<${Identifier("qbrp", messageId)}>>")
        ServerPlayNetworking.registerGlobalReceiver(Identifier("qbrp", messageId)) { server, player, handler, buf, responseSender ->
            try {
                val messageType = createMessageType(buf)
                val message = Message(messageId, messageType)
                val context = ServerReceiverContext(messageType.messageId, server, player, handler, responseSender)
                handle(message, context)
                if (callback(message, context as T, this) != true) {
                    callbackOnError?.invoke(message, context, this)
                }
            } catch (e: Exception) {
                logger.error("Ошибка обработки пакета $messageId: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun response(content: BilateralContent, context: ServerReceiverContext, name: String) {
        val markedMessage = Message(name, content.apply { messageId = context.id })
        NetworkManager.sendMessage(context.player, markedMessage)
    }

    fun response(content: BilateralContent, context: ServerReceiverContext) {
        val markedMessage = Message(messageId, content.apply { messageId = context.id })
        NetworkManager.sendMessage(context.player, markedMessage)
    }

    private fun handle(message: Message, context: ServerReceiverContext) {
        logger.log("${context.player.name.string} --> <<${message.identifier}>> (${message.content})")
    }
}