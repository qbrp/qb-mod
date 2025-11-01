package org.qbrp.client.core.notifications

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT)
class NotificationsModule : EngineModule("notifications") {
    override fun getKoinModule() = module {
        single<ClientNotificationSender> { ClientNotificationSenderImpl(MinecraftClient.getInstance().toastManager) }
    }
}