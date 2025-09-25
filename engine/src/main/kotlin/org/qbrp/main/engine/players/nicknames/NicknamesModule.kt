package org.qbrp.main.engine.players.nicknames

import com.mojang.brigadier.CommandDispatcher
import kotlinx.serialization.Serializable
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.construct.ComponentBuilderOperator.Companion.default
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext
import org.qbrp.main.engine.players.model.events.PlayerInitAfterJsonLoadEvent
import org.qbrp.main.engine.players.characters.components.CharactersBank

@Autoload(Priorities.PLAYERS - 1)
class NicknamesModule: GameModule("nicknames"), CommandRegistryEntry {
    override fun onLoad() {
        get<CommandsAPI>().add(NicknameCommand())
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(NicknameManager::class)
                serializeConstructor<NicknameManager.Data, ServerPlayerObject, NicknameManager> { builder, dto ->
                    NicknameManager(builder.componentContext, dto.customName)
                }
                standartConstructor<ServerPlayerObject> {
                    NicknameManager(it.componentContext)
                }
            }
        }

        withEventContext {
            on<PlayerInitAfterJsonLoadEvent> {
                it.player.default<NicknameManager>()
            }
        }

        get<CommandsAPI>().add(this)
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        NicknameCommand().register(dispatcher)
    }

    class NicknameManager(ctx: PlayerComponentInstantiateContext, var customName: String? = null): PlayerComponent(ctx) {
        @Serializable
        data class Data(val customName: String?) : DTO

        override val dto: DTO? get() = Data(customName)

        val defaultName: String get() = serverPlayer.username.value
        val displayName: String
            get() = customName
                ?: get<CharactersBank>()?.characterData?.formattedName
                ?: defaultName

        fun update(name: String) = withContext {
            customName = name
            playerManager.sendTabPlayerListPacket(uuid)
        }

        fun reset() {
            customName = null
        }
    }
}