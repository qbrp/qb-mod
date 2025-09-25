package org.qbrp.main.core.notifications

import org.qbrp.main.core.notifications.service.NotificationManager

interface NotificationContext {
    val notificationManager: NotificationManager
}