package org.qbrp.main.engine.synchronization

import org.qbrp.main.core.mc.player.PlayersAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModInitializedEvent
import org.qbrp.main.engine.synchronization.impl.Synchronizer
import org.qbrp.main.engine.synchronization.`interface`.SyncObjectProvider

@Autoload(LoadPriority.HIGHEST)
class SynchronizationModule: QbModule("synchronization"), SynchronizationAPI {
    init {
        dependsOn { Engine.isApiAvailable<PlayersAPI>() }
    }

    override fun getKoinModule() = inner<SynchronizationAPI>(this) {
        scoped { Synchronizer() }
    }

    override fun addProvider(provider: SyncObjectProvider) {
        getLocal<Synchronizer>().addProvider(provider)
    }

    override fun onEnable() {
        ModInitializedEvent.EVENT.register {
            getLocal<Synchronizer>().startTimer()
        }
    }
}