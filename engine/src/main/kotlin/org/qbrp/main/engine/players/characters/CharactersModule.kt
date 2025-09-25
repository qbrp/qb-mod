package org.qbrp.main.engine.players.characters

import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.characters.ApplyCharacterCommand
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.extensions.loadJson
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.engine.players.characters.entity.CharacterFactory
import org.qbrp.main.engine.players.characters.entity.CharacterProfile
import org.qbrp.main.engine.players.model.events.PlayerInstantiateEvent
import org.qbrp.main.engine.players.characters.events.CharacterApplyEvent
import org.qbrp.main.engine.players.characters.networking.CharacterApplyPacket

@Autoload(Priorities.PLAYERS - 3)
class CharactersModule: GameModule("characters") {
    override fun onLoad(): Unit = withEventContext {
        val packetSender = get<ServerPacketSender>()
        get<CommandsAPI>().add(listOf(getLocal<ApplyCharacterCommand>()))
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(CharactersBank::class)
                serializeConstructor<CharactersBank.Data, ServerPlayerObject, CharactersBank> { b, dto ->
                    val profiles = dto.characters.map { profileDto ->
                        val state = ComponentState<CharacterProfile>()
                        val profile = CharacterProfile(b, profileDto.id, profileDto.data, state)
                        state.loadJson(profileDto.state, profile)
                        state.parent = profile
                        profile
                    }.toMutableList()
                    CharactersBank(b.componentContext, profiles)
                }
            }
        }

        on<PlayerInstantiateEvent> {
            player.buildWithCtx {
                factory { ctx -> CharactersBank(ctx) }
                handle {
                    val characters = player.account.characters
                    val character = player.account.appliedCharacter
                    if (characters.isNotEmpty()) it.setCharacterOrBuild(character!!, get())
                }
            }
        }

        on<CharacterApplyEvent> {
            packetSender.sendImmediately(CharacterApplyPacket(character), CHARACTER_APPLY_CHANNEL, player as ServerPlayerObject)
        }
    }

    override fun getKoinModule() = innerWithApi( {
        single { CharacterFactory() }
    }, {
        scoped { ApplyCharacterCommand(this@CharactersModule) }
    })

    fun setCharacter(player: ServerPlayerObject, characterData: CharacterData) = withEventContext {
        player.require<CharactersBank>().setCharacterOrBuild(characterData, get())
    }

    companion object {
        const val CHARACTER_APPLY_CHANNEL = "character_apply"
    }
}