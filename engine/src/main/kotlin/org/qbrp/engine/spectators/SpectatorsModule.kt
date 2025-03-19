package org.qbrp.engine.spectators

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.engine.spectators.respawn.RespawnManager
import org.qbrp.engine.spectators.respawn.SpectatorCommands
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule
import org.qbrp.system.utils.log.Loggers

@Autoload
class SpectatorsModule: QbModule("spectators") {
    private lateinit var spectatorRespawn: RespawnManager
    private val logger = Loggers.get("spectatorsModule")

    override fun getKoinModule() = module {
        single { RespawnManager() }
        single { SpectatorCommands() }
    }

    override fun load() {
        CommandsRepository.add(get<SpectatorCommands>())
        spectatorRespawn = get()
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            spectatorRespawn.giveSpectator(handler.player)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            spectatorRespawn.cachePlayerGameMode(handler.player)
        }
    }
}