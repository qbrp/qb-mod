package org.qbrp.client.core.notifications

import net.minecraft.client.toast.SystemToast
import net.minecraft.client.toast.ToastManager
import org.qbrp.main.core.mc.text.emptyMcText
import org.qbrp.main.core.mc.text.map
import org.qbrp.main.core.mc.text.miniMessage
import org.qbrp.main.core.notifications.entity.Notification
import org.qbrp.main.core.utils.format.Format.formatMm
import org.qbrp.main.engine.text.literal

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