package org.qbrp.client.core.notifications

import net.minecraft.client.toast.SystemToast
import net.minecraft.client.toast.ToastManager
import org.qbrp.main.core.fabricmc.text.map
import org.qbrp.main.engine.text.literal
import org.qbrp.main.network.player.Notification

class ClientNotificationSenderImpl(private val toastManager: ToastManager): ClientNotificationSender {
    override fun send(notification: Notification) {
        val titleText = literal(notification.title).map()
        val contentText = notification.text.map()

        val toast = SystemToast(
            SystemToast.Type.TUTORIAL_HINT,
            titleText,
            contentText
        )

        toastManager.add(toast)
    }
}