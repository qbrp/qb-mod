package org.qbrp.main.engine.players.model

import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.fabricmc.api.EnvType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.construct.ComponentBuilderOperator.Companion.default
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.components.extensions.tick
import org.qbrp.main.core.components.extensions.tickExact
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.engine.players.model.service.PlayerSerializer
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.players.model.registration.AccountSyncCommand
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.storage.TableRegistry
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.core.game.GameTicker
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.saving.ServerStopSaver
import org.qbrp.main.core.game.saving.TimerSaver
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.mc.player.events.MinecraftPlayerLeaveEvent
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.players.auth.event.SuccessLoginEvent
import org.qbrp.main.engine.players.model.events.PlayerInitBeforeJsonLoadEvent
import org.qbrp.main.engine.players.model.mapping.PlayerListMapper
import org.qbrp.main.engine.players.model.networking.PlayerSynchronizer
import org.qbrp.main.engine.players.model.networking.ServerPlayerNetworkHandler
import org.qbrp.main.engine.players.model.service.PlayerConnectionHandler
import org.qbrp.main.engine.players.model.service.PlayerFactory
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.PlayerSaver

@Autoload(Priorities.PLAYERS, EnvType.SERVER)
class PlayersModule: EngineModule("players"), CommandRegistryEntry {
    companion object {
        const val INSTANTIATE_CHANNEL = "player-instantiate"
        const val PLAYER_LIST_CHANNEL = "player-list"
    }

    override fun getKoinModule(): Module = innerWithApi({
        single { ServerPlayerStorage() }
        single<PlayerList> { get<ServerPlayerStorage>() }
        single { PlayerListMapper() }
    }, {
        scoped<TableAccess> { get<TableRegistry>().getTable("players") }
        scoped { PlayerSerializer() }
        scoped {
            PlayerFactory(get(), get())
        }
        scoped { PlayerSaver(get(), get()) }
        scoped { ServerPlayerNetworkHandler(get(), get(),get(),get()) }
        scoped {
            PlayerConnectionHandler(
                get(), get(), get(), get(),
                get(), getLocal<PlayerSaver>(),
                get()
            )
        }
    })

    private val connectionHandlerCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onEnable() = withCoreContext {
        get<CommandsAPI>().add(this)
        val storage = getLocal<ServerPlayerStorage>()
        val connectionHandler = getLocal<PlayerConnectionHandler>()

        TimerSaver<ServerPlayerObject>("Players", 4000L * 60L)
            .run(storage, getLocal<PlayerSaver>())
        ServerStopSaver<ServerPlayerObject>()
            .run(storage, getLocal<PlayerSaver>())

        withEventContext {
            on<MinecraftPlayerLeaveEvent> {
                connectionHandler.handleDisconnect(it.player)
            }

            on<PlayerInitBeforeJsonLoadEvent> {
                player.default<PlayerSynchronizer>()
                player.add(PlayerLocation(player.componentContext))
            }

            on<SuccessLoginEvent> {
                connectionHandlerCoroutineScope.launch {
                    connectionHandler.handleAuth(
                        it.player,
                        it.account,
                        it.session
                    )
                }
            }

            on<ModLoadedEvent> {
                val containerService by inject<ContainerService>()
                containerService.addProvider(storage)
            }
        }

        val tickTask = object : Tick<ServerWorld> {
            override fun tick(context: ServerWorld) {
                storage.getAll().forEach {
                    it.tick()
                    it.tickExact()
                }
            }
        }
        get<GameTicker>().addWorldTickTask(tickTask)

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(PlayerSynchronizer::class)
                standartConstructor<ServerPlayerObject> {
                    PlayerSynchronizer(it.componentContext, get(), get(), get(), get())
                }
            }
        }

        componentRegistrar {
            use<PlayerLocation>()
        }
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        AccountSyncCommand(getLocal()).register(dispatcher)
    }
}