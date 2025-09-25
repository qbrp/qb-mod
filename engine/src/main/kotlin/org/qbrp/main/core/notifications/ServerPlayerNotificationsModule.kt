package org.qbrp.main.core.notifications

import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.notifications.service.NotificationManager
import org.qbrp.main.core.notifications.service.NotificationManagerImpl

@Autoload
class ServerPlayerNotificationsModule: EngineModule("server-player-notifications") {
    override fun getKoinModule(): Module = module {
        single<NotificationManager> { NotificationManagerImpl(get(), get(), get()) }
    }

    companion object {
        const val NOTIFICATION_CHANNEL = "notifications"
    }
}