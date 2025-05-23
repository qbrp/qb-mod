package org.qbrp.engine.players.characters

import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.prefabs.PrefabField
import org.qbrp.core.mc.player.PlayerObject
import org.qbrp.core.mc.player.registration.PlayerRegistrationCallback
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.engine.Engine
import org.qbrp.engine.players.characters.appearance.AppearanceManager
import org.qbrp.engine.characters.ApplyCharacterCommand
import org.qbrp.engine.characters.ApplyLookCommand
import org.qbrp.engine.characters.model.CharacterData
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.GameModule

@Autoload
class CharactersModule: GameModule("characters") {
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun onLoad() {
        PlayerRegistrationCallback.EVENT.register { session, manager ->
            if (!session.account.characters.isEmpty()) {
                setCharacter(session, session.account.appliedCharacter!!)
            }
        }
        CommandsRepository.add(listOf(get<ApplyCharacterCommand>(), get<ApplyLookCommand>()))
        get<AppearanceManager>().apply {
            registerKeybindHandler()
        }
    }

    override fun registerComponents(registry: ComponentsRegistry) {
        registry.register(Character::class.java)
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