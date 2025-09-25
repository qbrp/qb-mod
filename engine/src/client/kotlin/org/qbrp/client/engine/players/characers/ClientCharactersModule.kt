package org.qbrp.client.engine.players.characers

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.getClientPlayer
import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.characters.events.CharacterApplyEvent
import org.qbrp.main.engine.players.characters.CharactersModule
import org.qbrp.main.engine.players.characters.networking.CharacterApplyPacket
import org.qbrp.main.engine.players.characters.networking.CharacterPacketListener
import org.qbrp.main.mechanics.characters.model.CharacterData

@Autoload(env = EnvType.CLIENT)
class ClientCharactersModule() : EngineModule("client-characters-module"), CharacterPacketListener, ClientCharactersBank {
    override val character: CharacterData? get() = _character
    private var _character: CharacterData? = null

    override fun getKoinModule(): Module = module {
        single<ClientCharactersBank> { this@ClientCharactersModule }
    }

    override fun onEnable() {
        get<PacketListenerActivator<ClientContext>>().run(
            CharactersModule.CHARACTER_APPLY_CHANNEL,
            this,
            { buf, ctx -> CharacterApplyPacket(buf, getClientPlayer()) }
        )
    }

    override fun onCharacterApply(
        character: CharacterData,
        player: PlayerObject
    ): Unit = withEventContext {
        _character = character
        CharacterApplyEvent(player, character).emit()
    }
}