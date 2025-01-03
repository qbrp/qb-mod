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
    val callback: (Message, ServerReceiverContext) -> Unit
) {
    private val logger = Loggers.get("network", "receiving")

    fun register() {
        ServerPlayNetworking.registerGlobalReceiver(Identifier("qbrp", messageId)) { server, player, handler, buf, responseSender ->
            val context = ServerReceiverContext(server, player, handler, responseSender)
            try {
                val messageType = createMessageType(buf)
                val message = Message(messageId.toString(), messageType as MessageType)
                handle(message, context)
                callback(message, context)
            } catch (e: Exception) {
                logger.error("Ошибка обработки пакета $messageId: ${e.message}")
            }
        }
    }

    private fun createMessageType(buf: PacketByteBuf): Any {
        val constructor = messageTypeClass.constructors.firstOrNull()
            ?: throw IllegalArgumentException("Конструктор не найден для класса ${messageTypeClass}")

        val messageType = constructor.call() // Создание объекта
        val jsonObject = (messageType as MessageType).convertByteBuf(buf)
        return messageTypeClass.constructors.first { it.parameters.size == 1 }
            .call(jsonObject)
    }

    private fun handle(message: Message, context: ServerReceiverContext) {
        logger.log("${context.player.name.string} --> <<${message.id}>> (${message.content})")
    }

    data class ServerReceiverContext(
        val server: MinecraftServer,
        val player: ServerPlayerEntity,
        val handler: ServerPlayPacketListener,
        val responseSender: PacketSender
    )
}
