package org.qbrp.main.engine.players.characters

import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.core.modules.Autoload
import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.extensions.loadJson
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.players.characters.components.LookManager
import org.qbrp.main.engine.players.characters.components.Skin
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.engine.players.characters.components.characterComponentSerialize
import org.qbrp.main.engine.players.characters.components.CharacterPhysicalParameters
import org.qbrp.main.engine.players.characters.components.Height

@Autoload(Priorities.PLAYERS - 3)
class CharactersModule : EngineModule("characters") {
    override fun onEnable() {
        registerMessageSendColoring(get())
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(CharactersBank::class)
                serializeConstructor<CharactersBank.Data, ServerPlayerObject, CharactersBank> { b, dto ->
                    val profiles = dto.characters.map { profileDto ->
                        val state = ComponentState<CharacterRecord>()
                        val profile = CharacterRecord(b, profileDto.id, profileDto.data, state)
                        state.loadJson(profileDto.state, profile)
                        state.parent = profile
                        profile
                    }.toMutableList()
                    CharactersBank(b.componentContext, profiles)
                }
            }
            it.register { componentClass(Height::class) }
            it.register { data<CharacterPhysicalParameters>() }
            it.register {
                componentClass(LookManager::class)
                characterComponentSerialize<LookManager.Data, LookManager> { dto, ctx ->
                    LookManager(ctx = ctx, look = dto.look)
                }
            }
            it.register { componentClass(Skin::class) }
        }
    }
}