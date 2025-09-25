package org.qbrp.main.core.notifications.entity

import kotlinx.serialization.Serializable
import org.qbrp.main.core.mc.text.MinecraftTextMapper
import org.qbrp.main.core.notifications.entity.Notification.*
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.engine.text.map.TextMappingRegistry

@Serializable
data class NotificationDto(val text: TextDto, val title: String, val type: NotificationType)

fun Notification.mapToDto(textMapper: TextMappingRegistry): NotificationDto {
    return NotificationDto(textMapper.toDto(text), title, type)
}

fun NotificationDto.map(textMapper: TextMappingRegistry): Notification {
    return Notification(title, textMapper.fromDto(text), type)
}