package org.qbrp.client.engine.players

import net.fabricmc.api.EnvType
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.qbrp.client.core.mc.connection.event.DisconnectEvent
import org.qbrp.client.core.networking.preparation.CollectPreparationContributorsEvent
import org.qbrp.client.core.networking.preparation.PreparationContributor
import org.qbrp.client.core.networking.utils.enableClear
import org.qbrp.client.engine.players.networking.ClientPlayerFactory
import org.qbrp.client.engine.players.networking.ClientPlayerNetworkHandler
import org.qbrp.client.engine.players.networking.LocalPlayerPreparationContributor
import org.qbrp.client.engine.players.networking.PlayerResolver
import org.qbrp.client.engine.players.profile.PlayerProfileManager
import org.qbrp.client.engine.players.profile.PlayerProfileManagerImpl
import org.qbrp.client.engine.players.profile.PlayerProfileMapper
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent

@Autoload(priority = Priorities.MODULE - 1, env = EnvType.CLIENT)
class ClientPlayersModule: EngineModule("client-players") {
    override fun getKoinModule() = innerWithApi({
        single { ClientPlayerStorage().apply { enableClear() } }
        single<PlayerProfileManager> { PlayerProfileManagerImpl() }
        single<ClientPlayerManager> { getLocal<ClientPlayerManagerImpl>() }
    }, {
        scoped { PlayerProfileMapper() }
        scoped { ClientPlayerManagerImpl() }
        scoped { ClientPlayerFactory(get()) }
        scoped { PlayerResolver(get(), get(), get()) }
        scoped {
            ClientPlayerNetworkHandler(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    })

    override fun onEnable() = withEventContext {
        val playerManager: ClientPlayerManagerImpl = getLocal()
        val preparationContributor = LocalPlayerPreparationContributor(playerManager)
        on<ModLoadedEvent> { getLocal<ClientPlayerNetworkHandler>().run() }
        on<CollectPreparationContributorsEvent, PreparationContributor> { preparationContributor }
        on<DisconnectEvent> { playerManager.remove() }
    }
}