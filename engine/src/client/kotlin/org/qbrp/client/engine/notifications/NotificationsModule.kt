package org.qbrp.client.engine.notifications

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import org.koin.core.module.Module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.format.Format.asMiniMessage

@Autoload(env = EnvType.CLIENT)
class NotificationsModule : QbModule("notifications"), ClientNotificationsAPI {
    override fun sendSystemMessage(title: String, message: String, titleColor: String) {
        val toast = SystemToast(
            SystemToast.Type.TUTORIAL_HINT,
            "$titleColor$title".asMiniMessage(),
            message.asMiniMessage())
        MinecraftClient.getInstance().toastManager.add(toast)
    }

    override fun getKoinModule() = onlyApi<ClientNotificationsAPI>(this)
}