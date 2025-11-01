package org.qbrp.main.engine.players.model

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.EnvType
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.players.model.AccountSyncCommand
import org.qbrp.main.core.fabricmc.commands.CommandRegistryEntry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.storage.TableRegistry
import org.qbrp.main.core.fabricmc.commands.CommandsAPI
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.util.save.CoroutineSaver
import org.qbrp.main.core.util.save.runPeriodicSaver
import org.qbrp.main.core.util.save.runServerStopSaver
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.players.model.service.PlayerFactory
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.PlayerSaver
import org.qbrp.main.engine.players.model.service.PlayerService
import org.qbrp.main.engine.players.model.service.PlayersTable
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerService
import org.qbrp.main.engine.players.model.service.savePlayer

@Autoload(Priorities.PLAYERS, EnvType.SERVER)
class PlayersModule : EngineModule("players"), CommandRegistryEntry {
    override fun getKoinModule(): Module = module {
        single { ServerPlayerStorage() }
        single<ServerPlayerList> { get<ServerPlayerStorage>() }
        single<PlayerList> { get<ServerPlayerStorage>() }
        single { PlayerFactory(get()) }
        single { PlayersTable(get<TableRegistry>().getTable("players")) }
        single<PlayerSaver> {
            CoroutineSaver { player ->
                getLocal<PlayersTable>().savePlayer(player)
            }
        }
        single<PlayerService> { ServerPlayerService(get(), get(), get()) }
    }

    override fun onEnable() {
        get<CommandsAPI>().add(this)
        val storage = getLocal<ServerPlayerStorage>()
        val saver = get<PlayerSaver>()

        runServerStopSaver(storage, saver)
        runPeriodicSaver(storage, saver, "Players", 4000L * 60L)

        on<ModLoadedEvent> {
            val containerService by inject<ContainerService>()
            containerService.addProvider(storage)
        }
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        AccountSyncCommand(getLocal()).register(dispatcher)
    }
}