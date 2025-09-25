package org.qbrp.main.core.notifications.service

import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.notifications.entity.Notification

interface NotificationManager {
    fun send(player: PlayerReference, notification: Notification)
    fun sendAll(notification: Notification)
}