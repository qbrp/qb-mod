package org.qbrp.client.core.notifications

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.notifications.ServerPlayerNotificationsModule
import org.qbrp.main.core.notifications.entity.Notification
import org.qbrp.main.core.notifications.network.NotificationListener
import org.qbrp.main.core.notifications.network.NotificationPacket
import org.qbrp.main.engine.text.map.TextMappingRegistry

@Autoload(env = EnvType.CLIENT)
class NotificationsModule : EngineModule("notifications") {
    private val textMappingRegistry: TextMappingRegistry by inject()

    override fun onEnable() {
        get<PacketListenerActivator<ClientContext>>().run(
            ServerPlayerNotificationsModule.NOTIFICATION_CHANNEL,
            PacketListener(get()),
            { buf, ctx -> NotificationPacket(buf, textMappingRegistry) }
        )
    }

    override fun getKoinModule() = module {
        single<ClientNotificationSender> { ClientNotificationSenderImpl(MinecraftClient.getInstance().toastManager) }
    }

    class PacketListener(private val sender: ClientNotificationSender): NotificationListener {
        override fun onNotification(n: Notification) {
            sender.send(n)
        }
    }
}