package org.qbrp.main.core.notifications.network

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readProtobuf
import org.qbrp.main.core.networking.writeProtobuf
import org.qbrp.main.core.notifications.entity.Notification
import org.qbrp.main.core.notifications.entity.NotificationDto
import org.qbrp.main.core.notifications.entity.map
import org.qbrp.main.core.notifications.entity.mapToDto
import org.qbrp.main.engine.text.map.TextMappingRegistry

data class NotificationPacket(val notification: Notification, val textMapper: TextMappingRegistry): Packet<NotificationListener> {
    constructor(buf: PacketByteBuf,
                textMapper: TextMappingRegistry) : this(buf.readProtobuf<NotificationDto>().map(textMapper), textMapper)

    override fun write(buf: PacketByteBuf) {
        buf.writeProtobuf(notification.mapToDto(textMapper), NotificationDto.serializer())
    }

    override fun apply(listener: NotificationListener) {
        listener.onNotification(notification)
    }
}