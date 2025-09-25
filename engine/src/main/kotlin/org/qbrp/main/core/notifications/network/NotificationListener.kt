package org.qbrp.main.core.notifications.network

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.core.notifications.entity.Notification

interface NotificationListener: PacketListener {
    fun onNotification(n: Notification)
}