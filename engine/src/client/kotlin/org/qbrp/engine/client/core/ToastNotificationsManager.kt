package org.qbrp.engine.client.core

import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import org.qbrp.system.utils.format.Format.asMiniMessage

class ToastNotificationsManager: ClientNotifications {

    override fun sendSystemMessage(title: String, message: String, titleColor: String) {
        val toast = SystemToast(
            SystemToast.Type.TUTORIAL_HINT,
            "$titleColor$title".asMiniMessage(),
            message.asMiniMessage())
        MinecraftClient.getInstance().toastManager.add(toast)
    }
}