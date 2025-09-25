package org.qbrp.client.core.networking.preparation

import net.fabricmc.api.EnvType
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.mc.connection.event.ConnectEvent
import org.qbrp.client.core.mc.connection.event.DisconnectEvent
import org.qbrp.main.core.events.collect
import org.qbrp.main.core.events.on
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities

@Autoload(env = EnvType.CLIENT)
class SyncPreparationModule: EngineModule("sync-preparation"), SyncPreparation {
    private val contributors: MutableList<PreparationContributor> = mutableListOf()

    override val isReady: Boolean
        get() = contributors.all { it.prepared() }

    override fun onEnable() = withCoreContext {
        on<ConnectEvent> {
            contributors += CollectPreparationContributorsEvent().collect()
        }
        on<DisconnectEvent> {
            contributors.clear()
        }
    }

    override fun getKoinModule(): Module = module {
        single<SyncPreparation> { this@SyncPreparationModule }
    }
}