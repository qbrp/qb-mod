package org.qbrp.main.network.player

import kotlinx.serialization.Serializable
import net.minecraft.network.PacketByteBuf
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.engine.text.map.TextMappingRegistry
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.wrapEndpoint

private val textMappingRegistry by GlobalContext.get().inject<TextMappingRegistry>()

@ConsistentCopyVisibility
data class Notification internal constructor(val title: String, val text: TextComponent, val type: NotificationType) {
    companion object {
        fun error(title: String, context: String, exception: Exception): Notification {
            return Notification(title,
                literal("Контекст: $context. ${exception.message ?: "Неизвестная ошибка."}"),
                NotificationType.ERROR)
        }
        fun info(title: String, text: TextComponent): Notification {
            return Notification(title, text, NotificationType.INFO)
        }
    }
}

enum class NotificationType {
    ERROR, INFO
}

@Serializable
data class NotificationDto(val text: TextDto, val title: String, val type: NotificationType)

fun Notification.mapToDto(textMapper: TextMappingRegistry): NotificationDto {
    return NotificationDto(textMapper.toDto(text), title, type)
}

fun NotificationDto.map(textMapper: TextMappingRegistry): Notification {
    return Notification(title, textMapper.fromDto(text), type)
}

val NOTIFICATION_ENDPOINT = NetworkChannel<NotificationPacket>()
    .wrapEndpoint()

@Serializable
data class NotificationPacket(val notification: NotificationDto) : Packet

fun PlayerReference.sendNotification(notification: Notification) {
    NOTIFICATION_ENDPOINT
        .sendS2C(NotificationPacket(notification.mapToDto(textMappingRegistry)), this)
}

fun broadcastNotification(notification: Notification) {
    NOTIFICATION_ENDPOINT
        .broadcast(NotificationPacket(notification.mapToDto(textMappingRegistry)))
}