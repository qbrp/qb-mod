package org.qbrp.main.engine.spectators

import org.koin.dsl.module
import org.qbrp.main.engine.spectators.respawn.RespawnManager
import org.qbrp.main.engine.spectators.respawn.SpectatorCommands
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.events.on
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.core.keybinds.onKeybind
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.mc.player.events.AsyncMinecraftPlayerJoinEvent
import org.qbrp.main.core.mc.player.events.MinecraftPlayerLeaveEvent
import org.qbrp.main.core.mc.player.events.PlayerChangeGameModeEvent
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.engine.players.model.ServerPlayerObject

@Autoload
class SpectatorsModule: EngineModule("spectators") {
    private val respawnManager: RespawnManager by inject()

    override fun getKoinModule() = module {
        single { RespawnManager(get<ServerConfigData>().spectators, get()) }
        single { SpectatorCommands(get(), get()) }
    }

    override fun onEnable() = withCoreContext {
        get<CommandsAPI>().add(getLocal<SpectatorCommands>())

        on<AsyncMinecraftPlayerJoinEvent> {
            respawnManager.giveSpectator(it.player)
        }
        on<MinecraftPlayerLeaveEvent> {
            respawnManager.cachePlayerGameMode(it.player)
        }
        onKeybind(KeybindId("spectators_spawn")) {
            respawnManager.spawn(it)
        }
        on<PlayerChangeGameModeEvent> {
            if (it.gameMode != MinecraftGameMode.SPECTATOR) respawnManager.ignore(it.player as ServerPlayerObject)
        }
    }
}