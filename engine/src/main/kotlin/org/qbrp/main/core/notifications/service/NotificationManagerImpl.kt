package org.qbrp.main.core.notifications.service

import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.networking.sendAll
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.notifications.ServerPlayerNotificationsModule
import org.qbrp.main.core.notifications.entity.Notification
import org.qbrp.main.core.notifications.network.NotificationPacket
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.text.map.TextMappingRegistry

class NotificationManagerImpl(private val players: PlayerList,
                              private val packetSender: ServerPacketSender,
                              private val textMapper: TextMappingRegistry): NotificationManager {
    override fun send(player: PlayerReference, notification: Notification) {
        packetSender.sendImmediately(
            NotificationPacket(notification, textMapper),
            ServerPlayerNotificationsModule.NOTIFICATION_CHANNEL,
            player
        )
    }

    override fun sendAll(notification: Notification) {
        packetSender.sendAll(
            NotificationPacket(notification, textMapper),
            ServerPlayerNotificationsModule.NOTIFICATION_CHANNEL,
            players
        )
    }
}