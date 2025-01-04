package org.qbrp.system.networking

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

import org.qbrp.system.utils.log.Loggers
import kotlin.reflect.KClass

class ServerReceiver(
    val messageId: String,
    val messageTypeClass: KClass<*>,
    val callback: (Message, ServerReceiverContext, ServerReceiver) -> Unit
) {
    private val logger = Loggers.get("network", "receiving")

    fun register() {
        ServerPlayNetworking.registerGlobalReceiver(Identifier("qbrp", messageId)) { server, player, handler, buf, responseSender ->
            try {
                val messageType = createMessageType(buf)
                val message = Message(messageId, messageType as MessageContent)
                val context = ServerReceiverContext(server, player, handler, responseSender, messageType.messageId)
                handle(message, context)
                callback(message, context, this)
            } catch (e: Exception) {
                logger.error("Ошибка обработки пакета $messageId: ${e.message}")
            }
        }
    }

    fun response(message: Message, context: ServerReceiverContext) {
        val markedMessage = message.content.apply { messageId = context.id }
        NetworkManager.sendMessage(context.player, Message(messageId, markedMessage))
    }

    private fun createMessageType(buf: PacketByteBuf): Any {
        val noArgConstructor = messageTypeClass.constructors.firstOrNull { it.parameters.isEmpty() }
            ?: throw IllegalArgumentException("Конструктор без параметров не найден для класса ${messageTypeClass}")
        val messageContent = noArgConstructor.call() as MessageContent
        messageContent.convertByteBuf(buf)
        return messageContent
    }

    private fun handle(message: Message, context: ServerReceiverContext) {
        logger.log("${context.player.name.string} --> <<${message.identifier}>> (${message.content})")
    }

    data class ServerReceiverContext(
        val server: MinecraftServer,
        val player: ServerPlayerEntity,
        val handler: ServerPlayPacketListener,
        val responseSender: PacketSender,
        val id: String
    )
}
