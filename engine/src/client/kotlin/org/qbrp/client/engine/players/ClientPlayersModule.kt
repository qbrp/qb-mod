package org.qbrp.client.engine.players

import net.fabricmc.api.EnvType
import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.client.core.mc.connection.DisconnectEvent
import org.qbrp.client.core.networking.utils.clearOnDisconnect
import org.qbrp.client.engine.players.profile.PlayerProfileManager
import org.qbrp.client.engine.players.profile.PlayerProfileManagerImpl
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.engine.players.model.service.PlayerList

@Autoload(priority = Priorities.MODULE - 1, env = EnvType.CLIENT)
class ClientPlayersModule() : EngineModule("client-players"), ClientCharactersBank {
    override var character: CharacterData? = null

    override fun getKoinModule() = module {
        single { ClientPlayerStorage().apply { clearOnDisconnect() } }
        single<PlayerList> { get<ClientPlayerStorage>() }
        single<ClientPlayerList> { get<ClientPlayerStorage>() }
        single<PlayerProfileManager> { PlayerProfileManagerImpl() }
        single<ClientCharactersBank> { this@ClientPlayersModule }
        single<ClientPlayerManager> { ClientPlayerManagerImpl(get()) }
        single { ClientWorld(get()) }
        single { ClientPlayerFactory(get(), get()) }
    }

    override fun onEnable() {
        val clientPlayerManager: ClientPlayerManager by inject()
        on<DisconnectEvent> { clientPlayerManager.destroy() }

        componentRegistrar {
            use<LocalPlayerPosition>()
        }
    }

    override fun applyCharacter(character: CharacterData) {
        this.character = character
    }
}