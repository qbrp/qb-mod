package org.qbrp.main.engine.players.characters

import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.registration.PlayerRegistrationCallback
import org.qbrp.main.core.mc.player.PlayersAPI
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.players.characters.appearance.AppearanceManager
import org.qbrp.main.engine.characters.ApplyCharacterCommand
import org.qbrp.main.engine.characters.ApplyLookCommand
import org.qbrp.main.engine.characters.model.CharacterData
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.koin.core.component.get
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.engine.players.characters.appearance.Appearance

@Autoload
class CharactersModule: GameModule("characters") {
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
        dependsOn { Core.isApiAvailable<PlayersAPI>() }
    }

    override fun onLoad() {
        PlayerRegistrationCallback.EVENT.register { session, manager ->
            if (!session.account.characters.isEmpty()) {
                setCharacter(session, session.account.appliedCharacter!!)
            }
        }
        get<CommandsAPI>().add(listOf(getLocal<ApplyCharacterCommand>(), getLocal<ApplyLookCommand>()))
        getLocal<AppearanceManager>().apply {
            registerKeybindHandler()
        }
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(Character::class.java)
            it.register(Appearance::class.java)
        }
    }

    override fun getKoinModule() = module {
        single { ApplyCharacterCommand(this@CharactersModule) }
        single { ApplyLookCommand() }
        single { AppearanceManager() }
    }

    fun setCharacter(player: PlayerObject, characterData: CharacterData) {
        player.state.replaceComponent { Character(characterData) }
    }
}