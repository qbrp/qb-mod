package org.qbrp.client.core.notifications

import org.qbrp.main.core.notifications.entity.Notification

interface ClientNotificationSender {
    fun send(notification: Notification)
}