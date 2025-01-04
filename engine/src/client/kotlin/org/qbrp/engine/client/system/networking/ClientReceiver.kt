package org.qbrp.system.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import org.qbrp.system.utils.log.Loggers
import kotlin.reflect.KClass

class ClientReceiver(
    val messageId: String,
    val messageTypeClass: KClass<*>,
    val callback: (Message, ClientReceiverContext) -> Unit
) {
    private val logger = Loggers.get("network", "receiving")

    fun register() {
        ClientPlayNetworking.registerReceiver(Identifier("qbrp", messageId)) { client, handler, buf, responseSender ->
            val context = ClientReceiverContext(client, handler)
            try {
                val messageType = createMessageType(buf)
                val message = Message(messageId, messageType as MessageContent)
                handle(message, context)
                callback(message, context)
            } catch (e: Exception) {
                logger.error("Ошибка обработки пакета $messageId: ${e.message}")
            }
        }
    }

    private fun createMessageType(buf: PacketByteBuf): Any {
        // Используем только конструктор без параметров
        val noArgConstructor = messageTypeClass.constructors.firstOrNull { it.parameters.isEmpty() }
            ?: throw IllegalArgumentException("Конструктор без параметров не найден для класса $messageTypeClass")
        val messageContent = noArgConstructor.call() as MessageContent
        messageContent.convertByteBuf(buf)
        return messageContent
    }

    private fun handle(message: Message, context: ClientReceiverContext) {
        logger.log("${context.client.player?.name?.string} --> <<${message.identifier}>> (${message.content})")
    }

    data class ClientReceiverContext(
        val client: MinecraftClient,
        val handler: ClientPlayNetworkHandler
    )
}
