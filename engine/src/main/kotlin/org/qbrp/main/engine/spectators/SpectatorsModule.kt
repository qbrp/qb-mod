package org.qbrp.main.engine.spectators

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.koin.dsl.module
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.engine.spectators.respawn.RespawnManager
import org.qbrp.main.engine.spectators.respawn.SpectatorCommands
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.log.LoggerUtil
import org.koin.core.component.get
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.Core
import org.qbrp.main.core.mc.actionbar.ActionBarAPI
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload
class SpectatorsModule: QbModule("spectators") {
    private lateinit var spectatorRespawn: RespawnManager

    init {
        dependsOn { Core.isApiAvailable<ActionBarAPI>() }
    }

    override fun getKoinModule() = module {
        single { RespawnManager(get<ServerConfigData>().spectators, get()) }
        single { SpectatorCommands() }
    }

    override fun onEnable() {
        get<CommandsAPI>().add(getLocal<SpectatorCommands>())
        spectatorRespawn = getLocal()
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            spectatorRespawn.giveSpectator(handler.player)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            spectatorRespawn.cachePlayerGameMode(handler.player)
        }
    }
}