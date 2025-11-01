package org.qbrp.client.core.notifications

import org.qbrp.main.network.player.Notification

interface ClientNotificationSender {
    fun send(notification: Notification)
}